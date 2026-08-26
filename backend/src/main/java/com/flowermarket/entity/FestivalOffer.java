package com.flowermarket.entity;

import com.flowermarket.enums.FestivalType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "festival_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FestivalOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType festivalType;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Double discountPercent;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Builder.Default
    private boolean active = true;
}
