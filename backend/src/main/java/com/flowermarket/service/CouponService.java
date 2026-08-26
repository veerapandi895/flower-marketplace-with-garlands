package com.flowermarket.service;

import com.flowermarket.dto.CouponRequest;
import com.flowermarket.entity.Coupon;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon create(CouponRequest req) {
        if (couponRepository.findByCodeIgnoreCase(req.getCode()).isPresent()) {
            throw new BadRequestException("Coupon code already exists");
        }
        Coupon coupon = Coupon.builder()
                .code(req.getCode().toUpperCase())
                .description(req.getDescription())
                .discountPercent(req.getDiscountPercent())
                .maxDiscountAmount(req.getMaxDiscountAmount())
                .minOrderAmount(req.getMinOrderAmount())
                .validFrom(req.getValidFrom())
                .validTill(req.getValidTill())
                .usageLimit(req.getUsageLimit())
                .build();
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    public void deactivate(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    /** Validates coupon against order subtotal and returns the discount amount. */
    public double applyCoupon(String code, double subtotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isActive()) throw new BadRequestException("Coupon is no longer active");
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom()))
            throw new BadRequestException("Coupon is not valid yet");
        if (coupon.getValidTill() != null && now.isAfter(coupon.getValidTill()))
            throw new BadRequestException("Coupon has expired");
        if (coupon.getMinOrderAmount() != null && subtotal < coupon.getMinOrderAmount())
            throw new BadRequestException("Order does not meet minimum amount of ₹" + coupon.getMinOrderAmount());
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit())
            throw new BadRequestException("Coupon usage limit reached");

        double discount = subtotal * (coupon.getDiscountPercent() / 100.0);
        if (coupon.getMaxDiscountAmount() != null) {
            discount = Math.min(discount, coupon.getMaxDiscountAmount());
        }

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return Math.round(discount * 100.0) / 100.0;
    }
}
