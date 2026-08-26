package com.flowermarket.repository;

import com.flowermarket.entity.User;
import com.flowermarket.entity.Wishlist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Get customer's wishlist
    List<Wishlist> findByCustomer(User customer);

    // Check flower already exists
    boolean existsByCustomerIdAndFlowerId(Long customerId, Long flowerId);

    // Check garland already exists
    boolean existsByCustomerIdAndGarlandId(Long customerId, Long garlandId);

    // Delete flower from wishlist
    @Transactional
    @Modifying
    void deleteByCustomerIdAndFlowerId(Long customerId, Long flowerId);

    // Delete garland from wishlist
    @Transactional
    @Modifying
    void deleteByCustomerIdAndGarlandId(Long customerId, Long garlandId);
}