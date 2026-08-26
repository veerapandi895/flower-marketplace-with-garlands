package com.flowermarket.controller;

import com.flowermarket.dto.FestivalOfferRequest;
import com.flowermarket.entity.FestivalOffer;
import com.flowermarket.service.FestivalOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/festival-offers")
@RequiredArgsConstructor
public class AdminFestivalOfferController {

    private final FestivalOfferService festivalOfferService;

    @PostMapping
    public ResponseEntity<FestivalOffer> create(@RequestBody FestivalOfferRequest request) {
        return ResponseEntity.ok(festivalOfferService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FestivalOffer>> getAll() {
        return ResponseEntity.ok(festivalOfferService.getAll());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable Long id) {
        festivalOfferService.deactivate(id);
        return ResponseEntity.ok(Map.of("message", "Festival offer deactivated"));
    }
}
