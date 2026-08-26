package com.flowermarket.controller;

import com.flowermarket.entity.MarketPrice;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Unique Feature: Market Price - sellers can check the admin-published
// reference price for a given flower name before setting their own price.
@RestController
@RequestMapping("/api/seller/market-prices")
@RequiredArgsConstructor
public class SellerMarketPriceController {

    private final MarketPriceService marketPriceService;

    @GetMapping
    public ResponseEntity<MarketPrice> getForFlower(@RequestParam String flowerName) {
        return ResponseEntity.ok(marketPriceService.getLatestForFlower(flowerName)
                .orElseThrow(() -> new ResourceNotFoundException("No market price published for " + flowerName)));
    }
}
