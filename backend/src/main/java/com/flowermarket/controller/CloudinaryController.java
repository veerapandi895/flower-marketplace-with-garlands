package com.flowermarket.controller;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please select at least one image"));
        }

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "bloomcycle")
            );

            Object secureUrl = result.get("secure_url");

            if (secureUrl != null) {
                urls.add(secureUrl.toString());
            }
        }

        if (urls.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "No valid images were uploaded"));
        }

        return ResponseEntity.ok(
                Map.of("urls", urls)
        );
    }
}