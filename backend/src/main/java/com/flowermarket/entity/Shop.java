package com.flowermarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shops")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private User seller;

    @Column(nullable = false)
    private String shopName;

    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String gstNumber;

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer totalOrders = 0;

    private Double deliveryRadiusKm;

    private String businessHours; // e.g. "9:00 AM - 8:00 PM"

    @Builder.Default
    private boolean approved = true;
}
