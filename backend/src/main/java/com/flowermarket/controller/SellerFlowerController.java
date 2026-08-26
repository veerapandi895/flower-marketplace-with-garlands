package com.flowermarket.controller;

import com.flowermarket.dto.FlowerRequest;
import com.flowermarket.dto.FlowerResponse;
import com.flowermarket.entity.Flower;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.FlowerService;
import com.flowermarket.service.SmartPriceSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/flowers")
@RequiredArgsConstructor
public class SellerFlowerController {

    private final FlowerService flowerService;
    private final SmartPriceSuggestionService smartPriceSuggestionService;

    @PostMapping
    public ResponseEntity<FlowerResponse> add(@AuthenticationPrincipal UserPrincipal principal,
                                               @RequestBody FlowerRequest request) {
        Flower flower = flowerService.addFlower(principal.getUser(), request);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlowerResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable Long id,
                                                  @RequestBody FlowerRequest request) {
        Flower flower = flowerService.updateFlower(principal.getUser(), id, request);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id) {
        flowerService.deleteFlower(principal.getUser(), id);
        return ResponseEntity.ok(Map.of("message", "Flower deleted"));
    }

    @PatchMapping("/{id}/out-of-stock")
    public ResponseEntity<FlowerResponse> markOutOfStock(@AuthenticationPrincipal UserPrincipal principal,
                                                          @PathVariable Long id,
                                                          @RequestParam boolean value) {
        Flower flower = flowerService.markOutOfStock(principal.getUser(), id, value);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<FlowerResponse> setQuantity(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id,
                                                       @RequestParam Double value) {
        Flower flower = flowerService.setQuantity(principal.getUser(), id, value);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @GetMapping
    public ResponseEntity<List<FlowerResponse>> myFlowers(@AuthenticationPrincipal UserPrincipal principal) {
        List<FlowerResponse> flowers = flowerService.getSellerFlowers(principal.getUser().getId())
                .stream().map(flowerService::toResponse).toList();
        return ResponseEntity.ok(flowers);
    }

    // AI Flower Quality Check
    @PostMapping("/{id}/quality-check")
    public ResponseEntity<FlowerResponse> qualityCheck(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable Long id) {
        Flower flower = flowerService.runQualityCheck(principal.getUser(), id);
        return ResponseEntity.ok(flowerService.toResponse(flower));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<FlowerResponse>> lowStock(@AuthenticationPrincipal UserPrincipal principal,
                                                          @RequestParam(defaultValue = "10") double threshold) {
        List<FlowerResponse> flowers = flowerService.lowStockForSeller(principal.getUser().getId(), threshold)
                .stream().map(flowerService::toResponse).toList();
        return ResponseEntity.ok(flowers);
    }

    // Unique Feature: Smart Price Suggestion
    @GetMapping("/{id}/price-suggestion")
    public ResponseEntity<Map<String, String>> priceSuggestion(@PathVariable Long id) {
        Flower flower = flowerService.getById(id);
        return ResponseEntity.ok(Map.of("suggestion", smartPriceSuggestionService.suggest(flower)));
    }
}
