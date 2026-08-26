package com.flowermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // avoid Order -> items -> order -> items -> ... infinite recursion
    private Order order;

    // Exactly one of flower / garland is set for a given order item.
    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @ManyToOne
    @JoinColumn(name = "garland_id")
    private Garland garland;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double priceAtPurchase;
}
