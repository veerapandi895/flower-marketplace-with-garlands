package com.flowermarket.repository;

import com.flowermarket.entity.Order;
import com.flowermarket.entity.User;
import com.flowermarket.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findBySeller(User seller);
    List<Order> findBySellerAndStatus(User seller, OrderStatus status);
    long countByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.placedAt BETWEEN :start AND :end")
    List<Order> findByPlacedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.placedAt BETWEEN :start AND :end")
    Double sumRevenueBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.seller.id = :sellerId")
    Double sumRevenueBySeller(Long sellerId);
}
