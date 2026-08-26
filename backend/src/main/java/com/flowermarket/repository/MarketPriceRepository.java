package com.flowermarket.repository;

import com.flowermarket.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    List<MarketPrice> findByEffectiveDate(LocalDate date);
    Optional<MarketPrice> findTopByFlowerNameIgnoreCaseOrderByEffectiveDateDesc(String flowerName);
}
