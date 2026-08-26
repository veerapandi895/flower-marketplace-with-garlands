package com.flowermarket.controller;

import com.flowermarket.dto.FlowerResponse;
import com.flowermarket.dto.GarlandResponse;
import com.flowermarket.entity.Shop;
import com.flowermarket.service.FlowerService;
import com.flowermarket.service.GarlandService;
import com.flowermarket.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Phase 3: public shop profile - shows a seller's logo, banner and live listings. */
@RestController
@RequestMapping("/api/public/shops")
@RequiredArgsConstructor
public class PublicShopController {

    private final ShopService shopService;
    private final FlowerService flowerService;
    private final GarlandService garlandService;

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<Shop> getShop(@PathVariable Long sellerId) {
        return ResponseEntity.ok(shopService.getShopBySellerId(sellerId));
    }

    @GetMapping("/{sellerId}/flowers")
    public ResponseEntity<List<FlowerResponse>> getShopFlowers(@PathVariable Long sellerId) {
        List<FlowerResponse> flowers = flowerService.getAvailableSellerFlowers(sellerId)
                .stream().map(flowerService::toResponse).toList();
        return ResponseEntity.ok(flowers);
    }

    @GetMapping("/{sellerId}/garlands")
    public ResponseEntity<List<GarlandResponse>> getShopGarlands(@PathVariable Long sellerId) {
        List<GarlandResponse> garlands = garlandService.getAvailableSellerGarlands(sellerId)
                .stream().map(garlandService::toResponse).toList();
        return ResponseEntity.ok(garlands);
    }
}
