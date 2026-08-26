package com.flowermarket.entity;

import com.flowermarket.enums.GarlandStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garlands")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Garland {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private GarlandCategory category;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ElementCollection
    @CollectionTable(name = "garland_images", joinColumns = @JoinColumn(name = "garland_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private Double price;

    // Phase 5: live price after stock/festival/demand adjustments.
    // Recalculated by DynamicPricingService; falls back to price until first run.
    private Double currentPrice;

    // Weight in grams
    private Double weightGrams;

    @Column(nullable = false)
    private Integer availableQuantity;

    // e.g. "Rose + Jasmine + Marigold"
    @Column(length = 1000)
    private String flowerComposition;

    // In minutes
    private Integer estimatedPrepMinutes;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GarlandStatus status = GarlandStatus.ACTIVE;

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer orderCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
