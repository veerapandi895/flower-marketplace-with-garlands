package com.flowermarket.controller;

import com.flowermarket.dto.WasteRecordRequest;
import com.flowermarket.entity.WasteRecord;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Unique Feature: Waste Reduction pipeline for sellers to log routed flowers.
@RestController
@RequestMapping("/api/seller/waste")
@RequiredArgsConstructor
public class SellerWasteController {

    private final WasteService wasteService;

    @PostMapping
    public ResponseEntity<WasteRecord> record(@AuthenticationPrincipal UserPrincipal principal,
                                               @RequestBody WasteRecordRequest request) {
        return ResponseEntity.ok(wasteService.recordWaste(principal.getUser(), request));
    }
}
