package com.flowermarket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowermarket.entity.Flower;
import com.flowermarket.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI Flower Quality Check.
 *
 * Sends the flower's primary listing photo to Claude's vision API and asks
 * for a 0-100 freshness/quality score plus a short verdict (e.g. wilting,
 * discoloration, bruised petals, healthy blooms). Requires an Anthropic API
 * key configured via `app.ai.anthropic-api-key` in application.properties
 * (see https://docs.claude.com for how to obtain one and the current model
 * name to use for `app.ai.model` - model identifiers change over time so
 * this is intentionally left externally configurable rather than hardcoded).
 */
@Service
@Slf4j
public class AIQualityCheckService {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    @Value("${app.ai.anthropic-api-key:}")
    private String apiKey;

    @Value("${app.ai.model:claude-sonnet-4-5}")
    private String model;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record QualityResult(int score, String verdict) {}

    public QualityResult check(Flower flower) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException(
                    "AI quality check is not configured. Set app.ai.anthropic-api-key in application.properties.");
        }
        if (flower.getImages() == null || flower.getImages().isEmpty()) {
            throw new BadRequestException("Add at least one photo before running an AI quality check.");
        }

        String imageUrl = flower.getImages().get(0);
        byte[] imageBytes = loadImageBytes(imageUrl);
        String mediaType = guessMediaType(imageUrl);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = "You are inspecting a photo of flowers being sold on a marketplace listed as \"" +
                flower.getName() + "\". Rate the visible freshness and quality on a 0-100 scale " +
                "(100 = perfectly fresh, vibrant, no blemishes; 0 = wilted/rotten/unsellable). " +
                "Consider petal condition, color vibrancy, wilting, browning, and bruising. " +
                "Respond with ONLY a compact JSON object, no other text: " +
                "{\"score\": <integer 0-100>, \"verdict\": \"<one short sentence>\"}";

        String requestBody = buildRequestBody(prompt, mediaType, base64Image);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("AI quality check API error {}: {}", response.statusCode(), response.body());
                throw new BadRequestException("AI quality check failed (status " + response.statusCode() + ")");
            }

            return parseResult(response.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Could not reach AI quality check service: " + e.getMessage());
        }
    }

    private String buildRequestBody(String prompt, String mediaType, String base64Image) {
        // Built manually (rather than via Jackson tree) to keep this service dependency-free
        // beyond what Spring Boot already ships.
        return """
                {
                  "model": "%s",
                  "max_tokens": 300,
                  "messages": [
                    {
                      "role": "user",
                      "content": [
                        {"type": "image", "source": {"type": "base64", "media_type": "%s", "data": "%s"}},
                        {"type": "text", "text": %s}
                      ]
                    }
                  ]
                }
                """.formatted(model, mediaType, base64Image, quoteJson(prompt));
    }

    private String quoteJson(String text) {
        try {
            return objectMapper.writeValueAsString(text);
        } catch (IOException e) {
            throw new BadRequestException("Could not build AI request");
        }
    }

    private QualityResult parseResult(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode contentArray = root.path("content");
        StringBuilder text = new StringBuilder();
        if (contentArray.isArray()) {
            for (JsonNode block : contentArray) {
                if ("text".equals(block.path("type").asText())) {
                    text.append(block.path("text").asText());
                }
            }
        }

        String raw = text.toString().trim();
        // Model may wrap the JSON in prose/markdown fences despite instructions - extract the object.
        Matcher matcher = Pattern.compile("\\{.*}", Pattern.DOTALL).matcher(raw);
        if (!matcher.find()) {
            throw new BadRequestException("AI quality check returned an unexpected response");
        }

        JsonNode result = objectMapper.readTree(matcher.group());
        int score = Math.max(0, Math.min(100, result.path("score").asInt(0)));
        String verdict = result.path("verdict").asText("No verdict provided");
        return new QualityResult(score, verdict);
    }

    private byte[] loadImageBytes(String imageUrl) {
        try {
            if (imageUrl.startsWith("/uploads/")) {
                Path path = Paths.get(uploadDir, imageUrl.substring("/uploads/".length())).normalize();
                return Files.readAllBytes(path);
            }
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                HttpRequest request = HttpRequest.newBuilder(URI.create(imageUrl)).GET().build();
                HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    throw new BadRequestException("Could not download listing photo for quality check");
                }
                return response.body();
            }
            throw new BadRequestException("Unrecognized image reference: " + imageUrl);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Could not read listing photo: " + e.getMessage());
        }
    }

    private String guessMediaType(String imageUrl) {
        String lower = imageUrl.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
