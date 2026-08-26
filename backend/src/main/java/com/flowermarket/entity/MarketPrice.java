package com.flowermarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// Admin-updated daily market reference price per flower name, used by sellers
// as a benchmark ("Market Price" unique feature).
@Entity
@Table(name = "market_prices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String flowerName;

    @Column(nullable = false)
    private Double pricePerKg;

    @Column(nullable = false)
    private LocalDate effectiveDate;
}
