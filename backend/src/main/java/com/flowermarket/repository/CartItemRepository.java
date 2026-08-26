package com.flowermarket.repository;

import com.flowermarket.entity.CartItem;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(User customer);
    Optional<CartItem> findByCustomerAndFlower(User customer, Flower flower);
    Optional<CartItem> findByCustomerAndGarland(User customer, Garland garland);
    void deleteByCustomer(User customer);
}
