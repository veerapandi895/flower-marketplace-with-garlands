package com.flowermarket.repository;

import com.flowermarket.entity.Garland;
import com.flowermarket.enums.GarlandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GarlandRepository extends JpaRepository<Garland, Long> {

    List<Garland> findBySellerId(Long sellerId);

    List<Garland> findBySellerIdAndStatus(Long sellerId, GarlandStatus status);

    List<Garland> findByStatus(GarlandStatus status);

    @Query("SELECT g FROM Garland g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(g.flowerComposition) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Garland> searchByName(@Param("query") String query);

    @Query("SELECT g FROM Garland g WHERE g.category.id = :categoryId")
    List<Garland> findByCategoryId(@Param("categoryId") Long categoryId);

    List<Garland> findTop10ByOrderByOrderCountDesc();

    List<Garland> findTop10ByOrderByViewCountDesc();

    @Query("SELECT g FROM Garland g WHERE g.availableQuantity <= :threshold AND g.seller.id = :sellerId")
    List<Garland> findLowStockBySeller(@Param("sellerId") Long sellerId, @Param("threshold") Integer threshold);
}
