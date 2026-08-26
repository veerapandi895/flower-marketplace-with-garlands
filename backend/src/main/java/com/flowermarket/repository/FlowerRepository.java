package com.flowermarket.repository;

import com.flowermarket.entity.Flower;
import com.flowermarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlowerRepository extends JpaRepository<Flower, Long> {

    List<Flower> findBySeller(User seller);

    List<Flower> findBySellerId(Long sellerId);

    List<Flower> findBySellerIdAndAvailableTrueAndOutOfStockFalse(Long sellerId);

    List<Flower> findByAvailableTrueAndOutOfStockFalse();

    @Query("SELECT f FROM Flower f WHERE f.available = true AND f.outOfStock = false " +
           "AND LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Flower> searchByName(@Param("query") String query);

    @Query("SELECT f FROM Flower f WHERE f.category.id = :categoryId AND f.available = true AND f.outOfStock = false")
    List<Flower> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT f FROM Flower f WHERE f.available = true AND f.expiryDate IS NOT NULL AND f.expiryDate < :now")
    List<Flower> findExpiredButStillAvailable(@Param("now") java.time.LocalDateTime now);

    List<Flower> findTop10ByOrderByOrderCountDesc();

    List<Flower> findTop10ByOrderByViewCountDesc();

    @Query("SELECT f FROM Flower f WHERE f.quantity <= :threshold AND f.seller.id = :sellerId")
    List<Flower> findLowStockBySeller(@Param("sellerId") Long sellerId, @Param("threshold") Double threshold);

    @Query("SELECT f FROM Flower f WHERE f.quantity <= :threshold")
    List<Flower> findAllLowStock(@Param("threshold") Double threshold);
}
