package com.flowermarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(nullable = false)
    private Double discountPercent;

    private Double maxDiscountAmount;

    private Double minOrderAmount;

    private LocalDateTime validFrom;
    private LocalDateTime validTill;

    @Builder.Default
    private boolean active = true;

    private Integer usageLimit;

    @Builder.Default
    private Integer usedCount = 0;
}
