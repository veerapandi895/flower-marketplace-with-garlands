package com.flowermarket.entity;

import com.flowermarket.enums.PricedItemType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Phase 5: records every automatic price change made by DynamicPricingService. */
@Entity
@Table(name = "price_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricedItemType itemType;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Double price;

    // e.g. "Low stock (8 left) +12% | Festival active +10% | High demand +5%"
    @Column(length = 500)
    private String reason;

    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();
}
