package com.flowermarket.controller;

import com.flowermarket.dto.FlowerResponse;
import com.flowermarket.entity.Category;
import com.flowermarket.entity.FestivalOffer;
import com.flowermarket.entity.Flower;
import com.flowermarket.service.CategoryService;
import com.flowermarket.service.FestivalOfferService;
import com.flowermarket.service.FlowerService;
import com.flowermarket.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints reachable without authentication - browsing, search, comparison. */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final FlowerService flowerService;
    private final CategoryService categoryService;
    private final FestivalOfferService festivalOfferService;
    private final ReviewService reviewService;

    @GetMapping("/flowers")
    public ResponseEntity<List<FlowerResponse>> browse() {
        List<FlowerResponse> flowers = flowerService.getAllAvailable().stream()
                .map(flowerService::toResponse).toList();
        return ResponseEntity.ok(flowers);
    }

    @GetMapping("/flowers/{id}")
    public ResponseEntity<FlowerResponse> getFlower(@PathVariable Long id) {
        Flower flower = flowerService.incrementView(id);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @GetMapping("/flowers/search")
    public ResponseEntity<List<FlowerResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(flowerService.search(query).stream().map(flowerService::toResponse).toList());
    }

    @GetMapping("/flowers/category/{categoryId}")
    public ResponseEntity<List<FlowerResponse>> byCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(flowerService.byCategory(categoryId).stream().map(flowerService::toResponse).toList());
    }

    // Phase 8: combined filter panel - category, price range, seller, sort
    @GetMapping("/flowers/filter")
    public ResponseEntity<List<FlowerResponse>> filter(@RequestParam(required = false) Long categoryId,
                                                        @RequestParam(required = false) Double minPrice,
                                                        @RequestParam(required = false) Double maxPrice,
                                                        @RequestParam(required = false) Long sellerId,
                                                        @RequestParam(required = false) String sort) {
        List<FlowerResponse> flowers = flowerService.filter(categoryId, minPrice, maxPrice, sellerId, sort)
                .stream().map(flowerService::toResponse).toList();
        return ResponseEntity.ok(flowers);
    }

    // Unique Feature: Nearby Price Comparison - same flower name across shops
    @GetMapping("/flowers/compare")
    public ResponseEntity<List<FlowerResponse>> comparePrices(@RequestParam String name) {
        List<FlowerResponse> matches = flowerService.search(name).stream()
                .map(flowerService::toResponse)
                .sorted((a, b) -> Double.compare(a.getCurrentPrice(), b.getCurrentPrice()))
                .toList();
        return ResponseEntity.ok(matches);
    }

    // Unique Feature: Trending Flowers
    @GetMapping("/flowers/trending")
    public ResponseEntity<List<FlowerResponse>> trending() {
        return ResponseEntity.ok(flowerService.trending().stream().map(flowerService::toResponse).toList());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> categories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/festival-offers")
    public ResponseEntity<List<FestivalOffer>> activeOffers() {
        return ResponseEntity.ok(festivalOfferService.getActive());
    }

    @GetMapping("/reviews/flower/{flowerId}")
    public ResponseEntity<?> flowerReviews(@PathVariable Long flowerId) {
        return ResponseEntity.ok(reviewService.getFlowerReviews(flowerId));
    }

    // Phase 9: home page testimonials
    @GetMapping("/testimonials")
    public ResponseEntity<?> testimonials() {
        return ResponseEntity.ok(reviewService.getTestimonials());
    }
}
