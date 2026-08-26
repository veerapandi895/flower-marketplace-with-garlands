package com.flowermarket.controller;

import com.flowermarket.dto.GarlandRequest;
import com.flowermarket.dto.GarlandResponse;
import com.flowermarket.entity.Garland;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.GarlandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller/garlands")
@RequiredArgsConstructor
public class SellerGarlandController {

    private final GarlandService garlandService;

    @PostMapping
    public ResponseEntity<GarlandResponse> add(@AuthenticationPrincipal UserPrincipal principal,
                                                @RequestBody GarlandRequest request) {
        Garland garland = garlandService.addGarland(principal.getUser(), request);
        return ResponseEntity.ok(garlandService.toResponse(garland));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarlandResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long id,
                                                   @RequestBody GarlandRequest request) {
        Garland garland = garlandService.updateGarland(principal.getUser(), id, request);
        return ResponseEntity.ok(garlandService.toResponse(garland));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id) {
        garlandService.deleteGarland(principal.getUser(), id);
        return ResponseEntity.ok(Map.of("message", "Garland deleted"));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<GarlandResponse> setStock(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable Long id,
                                                     @RequestParam Integer value) {
        Garland garland = garlandService.setStock(principal.getUser(), id, value);
        return ResponseEntity.ok(garlandService.toResponse(garland));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<GarlandResponse> setActive(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long id,
                                                      @RequestParam boolean value) {
        Garland garland = garlandService.setActive(principal.getUser(), id, value);
        return ResponseEntity.ok(garlandService.toResponse(garland));
    }

    @GetMapping
    public ResponseEntity<List<GarlandResponse>> myGarlands(@AuthenticationPrincipal UserPrincipal principal) {
        List<GarlandResponse> garlands = garlandService.getSellerGarlands(principal.getUser().getId())
                .stream().map(garlandService::toResponse).toList();
        return ResponseEntity.ok(garlands);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<GarlandResponse>> lowStock(@AuthenticationPrincipal UserPrincipal principal,
                                                           @RequestParam(defaultValue = "5") int threshold) {
        List<GarlandResponse> garlands = garlandService.lowStockForSeller(principal.getUser().getId(), threshold)
                .stream().map(garlandService::toResponse).toList();
        return ResponseEntity.ok(garlands);
    }
}
