package com.flowermarket.controller;

import com.flowermarket.dto.GarlandResponse;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.GarlandCategory;
import com.flowermarket.service.GarlandCategoryService;
import com.flowermarket.service.GarlandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Public browsing endpoints for garlands - no authentication required. */
@RestController
@RequestMapping("/api/public/garlands")
@RequiredArgsConstructor
public class PublicGarlandController {

    private final GarlandService garlandService;
    private final GarlandCategoryService garlandCategoryService;

    @GetMapping
    public ResponseEntity<List<GarlandResponse>> browse(@RequestParam(required = false) String sort) {
        List<Garland> garlands = garlandService.sort(garlandService.getAllAvailable(), sort);
        return ResponseEntity.ok(garlands.stream().map(garlandService::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarlandResponse> getGarland(@PathVariable Long id) {
        Garland garland = garlandService.incrementView(id);
        return ResponseEntity.ok(garlandService.toResponse(garland));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GarlandResponse>> search(@RequestParam String query,
                                                         @RequestParam(required = false) String sort) {
        List<Garland> results = garlandService.sort(garlandService.search(query), sort);
        return ResponseEntity.ok(results.stream().map(garlandService::toResponse).toList());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<GarlandResponse>> byCategory(@PathVariable Long categoryId,
                                                             @RequestParam(required = false) String sort) {
        List<Garland> results = garlandService.sort(garlandService.byCategory(categoryId), sort);
        return ResponseEntity.ok(results.stream().map(garlandService::toResponse).toList());
    }

    // Phase 8: combined filter panel - category, price range, seller, sort
    @GetMapping("/filter")
    public ResponseEntity<List<GarlandResponse>> filter(@RequestParam(required = false) Long categoryId,
                                                         @RequestParam(required = false) Double minPrice,
                                                         @RequestParam(required = false) Double maxPrice,
                                                         @RequestParam(required = false) Long sellerId,
                                                         @RequestParam(required = false) String sort) {
        List<GarlandResponse> garlands = garlandService.filter(categoryId, minPrice, maxPrice, sellerId, sort)
                .stream().map(garlandService::toResponse).toList();
        return ResponseEntity.ok(garlands);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<GarlandResponse>> trending() {
        return ResponseEntity.ok(garlandService.trending().stream().map(garlandService::toResponse).toList());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<GarlandCategory>> categories() {
        return ResponseEntity.ok(garlandCategoryService.getAll());
    }
}
