package com.flowermarket.service;

import com.flowermarket.dto.MarketPriceRequest;
import com.flowermarket.entity.MarketPrice;
import com.flowermarket.repository.MarketPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Unique Feature: Market Price.
 * Admin publishes a daily reference price per flower; sellers can compare
 * their listing price against it.
 */
@Service
@RequiredArgsConstructor
public class MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;

    public MarketPrice publishToday(MarketPriceRequest req) {
        MarketPrice price = MarketPrice.builder()
                .flowerName(req.getFlowerName())
                .pricePerKg(req.getPricePerKg())
                .effectiveDate(LocalDate.now())
                .build();
        return marketPriceRepository.save(price);
    }

    public List<MarketPrice> getToday() {
        return marketPriceRepository.findByEffectiveDate(LocalDate.now());
    }

    public Optional<MarketPrice> getLatestForFlower(String flowerName) {
        return marketPriceRepository.findTopByFlowerNameIgnoreCaseOrderByEffectiveDateDesc(flowerName);
    }
}
