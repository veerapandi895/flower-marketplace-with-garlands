package com.flowermarket.repository;

import com.flowermarket.entity.Shop;
import com.flowermarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findBySeller(User seller);
    Optional<Shop> findBySellerId(Long sellerId);
}
