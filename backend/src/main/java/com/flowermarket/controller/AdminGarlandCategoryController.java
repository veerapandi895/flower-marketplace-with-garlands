package com.flowermarket.controller;

import com.flowermarket.dto.GarlandCategoryRequest;
import com.flowermarket.entity.GarlandCategory;
import com.flowermarket.service.GarlandCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/garland-categories")
@RequiredArgsConstructor
public class AdminGarlandCategoryController {

    private final GarlandCategoryService garlandCategoryService;

    @GetMapping
    public ResponseEntity<List<GarlandCategory>> getAll() {
        return ResponseEntity.ok(garlandCategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<GarlandCategory> create(@RequestBody GarlandCategoryRequest request) {
        return ResponseEntity.ok(garlandCategoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarlandCategory> update(@PathVariable Long id, @RequestBody GarlandCategoryRequest request) {
        return ResponseEntity.ok(garlandCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        garlandCategoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Garland category deleted"));
    }
}
