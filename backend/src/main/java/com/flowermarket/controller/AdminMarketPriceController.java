package com.flowermarket.controller;

import com.flowermarket.dto.MarketPriceRequest;
import com.flowermarket.entity.MarketPrice;
import com.flowermarket.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/market-prices")
@RequiredArgsConstructor
public class AdminMarketPriceController {

    private final MarketPriceService marketPriceService;

    @PostMapping
    public ResponseEntity<MarketPrice> publish(@RequestBody MarketPriceRequest request) {
        return ResponseEntity.ok(marketPriceService.publishToday(request));
    }

    @GetMapping("/today")
    public ResponseEntity<List<MarketPrice>> today() {
        return ResponseEntity.ok(marketPriceService.getToday());
    }
}
