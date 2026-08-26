package com.flowermarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Exactly one of flower / garland is set for a given cart item.
    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @ManyToOne
    @JoinColumn(name = "garland_id")
    private Garland garland;

    @Column(nullable = false)
    private Double quantity;

    // Price captured at time of adding to cart (dynamic price snapshot)
    @Column(nullable = false)
    private Double priceSnapshot;
}
