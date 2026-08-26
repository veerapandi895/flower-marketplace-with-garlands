package com.flowermarket.controller;

import com.flowermarket.dto.ShopRequest;
import com.flowermarket.entity.Shop;
import com.flowermarket.security.UserPrincipal;
import com.flowermarket.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/shop")
@RequiredArgsConstructor
public class SellerShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<Shop> createShop(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestBody ShopRequest request) {
        return ResponseEntity.ok(shopService.createShop(principal.getUser(), request));
    }

    @PutMapping
    public ResponseEntity<Shop> updateShop(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestBody ShopRequest request) {
        return ResponseEntity.ok(shopService.updateShop(principal.getUser(), request));
    }

    @GetMapping
    public ResponseEntity<Shop> getMyShop(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(shopService.getShopBySeller(principal.getUser()));
    }
}
