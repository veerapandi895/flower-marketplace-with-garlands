package com.flowermarket.controller;

import com.flowermarket.dto.ReviewRequest;
import com.flowermarket.entity.Review;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/reviews")
@RequiredArgsConstructor
public class CustomerReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> add(@AuthenticationPrincipal UserPrincipal principal,
                                       @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(principal.getUser(), request));
    }
}
