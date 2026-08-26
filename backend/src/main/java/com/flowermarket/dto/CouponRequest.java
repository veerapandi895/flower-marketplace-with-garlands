package com.flowermarket.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    private String code;
    private String description;
    private Double discountPercent;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validTill;
    private Integer usageLimit;
}
