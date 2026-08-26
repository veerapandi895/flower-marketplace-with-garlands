package com.flowermarket.controller;

import com.flowermarket.entity.NewsletterSubscriber;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.repository.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

/** Phase 9: home page newsletter signup. */
@RestController
@RequestMapping("/api/public/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final NewsletterSubscriberRepository newsletterSubscriberRepository;

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BadRequestException("Please enter a valid email address");
        }
        if (newsletterSubscriberRepository.existsByEmailIgnoreCase(email.trim())) {
            return ResponseEntity.ok(Map.of("message", "You're already subscribed!"));
        }
        newsletterSubscriberRepository.save(NewsletterSubscriber.builder().email(email.trim()).build());
        return ResponseEntity.ok(Map.of("message", "Subscribed! Watch your inbox for fresh flower deals."));
    }
}
