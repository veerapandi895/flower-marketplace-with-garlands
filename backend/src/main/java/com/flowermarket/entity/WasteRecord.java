package com.flowermarket.entity;

import com.flowermarket.enums.WasteDestination;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Tracks expired/unsold flowers routed for waste reduction (temple donation,
// NGO, compost, incense stick factory, natural color makers).
@Entity
@Table(name = "waste_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WasteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flower_id", nullable = false)
    private Flower flower;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WasteDestination destination;

    private String notes;

    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();
}
