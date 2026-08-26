package com.flowermarket.entity;

import com.flowermarket.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double subtotal;

    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(nullable = false)
    private Double totalAmount;

    private String couponCode;

    @Column(nullable = false)
    private String deliveryAddress;

    private String paymentMethod;

    @Builder.Default
    private boolean paid = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PLACED;

    @Builder.Default
    private LocalDateTime placedAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // Phase 12: order tracking
    private LocalDateTime estimatedDeliveryAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
}
