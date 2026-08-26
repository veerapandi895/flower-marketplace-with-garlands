package com.flowermarket.entity;

import com.flowermarket.enums.FlowerUnit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flowers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ElementCollection
    @CollectionTable(name = "flower_images", joinColumns = @JoinColumn(name = "flower_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // Base/original price set by seller (before dynamic pricing markdown)
    @Column(nullable = false)
    private Double basePrice;

    // Phase 5: live price after freshness markdown + stock/festival/demand adjustments.
    // Recalculated by DynamicPricingService; falls back to basePrice until first run.
    private Double currentPrice;

    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlowerUnit unit;

    // Hours after harvest during which flower is considered fully fresh
    @Column(nullable = false)
    private Integer freshnessHours;

    @Column(nullable = false)
    private LocalDateTime harvestDate;

    private LocalDateTime expiryDate;

    @Builder.Default
    private boolean available = true;

    @Builder.Default
    private boolean outOfStock = false;

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer orderCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // AI Quality Check (vision-based freshness/quality assessment of the listing photo)
    private Integer qualityScore;       // 0-100
    private String qualityVerdict;      // short human-readable summary
    private LocalDateTime qualityCheckedAt;
}
