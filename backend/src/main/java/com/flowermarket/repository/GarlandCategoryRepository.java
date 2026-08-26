package com.flowermarket.repository;

import com.flowermarket.entity.GarlandCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarlandCategoryRepository extends JpaRepository<GarlandCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
}
