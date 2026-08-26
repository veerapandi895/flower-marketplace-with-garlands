package com.flowermarket.controller;

import com.flowermarket.dto.CouponRequest;
import com.flowermarket.entity.Coupon;
import com.flowermarket.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> create(@RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAll() {
        return ResponseEntity.ok(couponService.getAll());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable Long id) {
        couponService.deactivate(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deactivated"));
    }
}
