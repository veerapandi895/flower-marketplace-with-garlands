package com.flowermarket.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Admin-managed garland categories, e.g. Wedding Garland, Temple Garland,
 * Rose Garland, Jasmine Garland, Reception Garland, VIP Garland,
 * Bride Garland, Groom Garland, Custom Garland.
 */
@Entity
@Table(name = "garland_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GarlandCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    private String imageUrl;
}
