package com.flowermarket.service;

import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.PriceHistory;
import com.flowermarket.enums.PricedItemType;
import com.flowermarket.repository.FestivalOfferRepository;
import com.flowermarket.repository.FlowerRepository;
import com.flowermarket.repository.GarlandRepository;
import com.flowermarket.repository.PriceHistoryRepository;
import com.flowermarket.util.DynamicPriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 5: Smart Dynamic Pricing.
 *
 * Rules (multiplicative, applied on top of the existing freshness markdown
 * for flowers - see DynamicPriceCalculator):
 *   Stock > 100                -> price decreases  (move surplus stock)
 *   Stock < 20                 -> price increases  (scarcity)
 *   A festival offer is active -> price increases   (demand spike)
 *   High demand (lots of orders relative to views) -> price increases
 *   Low demand  (few orders relative to views)      -> price decreases
 *
 * The combined multiplier is clamped to [0.7, 1.4] so automatic pricing
 * never swings wildly. Every time the computed price actually changes, a
 * PriceHistory row is written so sellers/admins can audit what happened.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingService {

    private static final double MIN_MULTIPLIER = 0.7;
    private static final double MAX_MULTIPLIER = 1.4;

    private final FlowerRepository flowerRepository;
    private final GarlandRepository garlandRepository;
    private final FestivalOfferRepository festivalOfferRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final DynamicPriceCalculator freshnessCalculator;

    public double recalculateFlowerPrice(Flower flower) {
        double freshnessPrice = freshnessCalculator.calculate(flower).price();

        List<String> reasons = new ArrayList<>();
        double multiplier = stockMultiplier(flower.getQuantity(), reasons)
                * festivalMultiplier(reasons)
                * demandMultiplier(flower.getOrderCount(), flower.getViewCount(), reasons);
        multiplier = clamp(multiplier);

        double newPrice = round2(freshnessPrice * multiplier);
        applyIfChanged(PricedItemType.FLOWER, flower.getId(), flower.getCurrentPrice(), newPrice, reasons);
        flower.setCurrentPrice(newPrice);
        return newPrice;
    }

    public double recalculateGarlandPrice(Garland garland) {
        List<String> reasons = new ArrayList<>();
        double multiplier = stockMultiplier(garland.getAvailableQuantity() == null ? 0 : garland.getAvailableQuantity(), reasons)
                * festivalMultiplier(reasons)
                * demandMultiplier(garland.getOrderCount(), garland.getViewCount(), reasons);
        multiplier = clamp(multiplier);

        double newPrice = round2(garland.getPrice() * multiplier);
        applyIfChanged(PricedItemType.GARLAND, garland.getId(), garland.getCurrentPrice(), newPrice, reasons);
        garland.setCurrentPrice(newPrice);
        return newPrice;
    }

    /** Recalculates every active flower and garland. Called by the scheduled job. */
    public void recalculateAll() {
        List<Flower> flowers = flowerRepository.findAll();
        for (Flower flower : flowers) {
            if (flower.isAvailable() && !flower.isOutOfStock()) {
                recalculateFlowerPrice(flower);
            }
        }
        flowerRepository.saveAll(flowers);

        List<Garland> garlands = garlandRepository.findAll();
        for (Garland garland : garlands) {
            recalculateGarlandPrice(garland);
        }
        garlandRepository.saveAll(garlands);

        log.info("Dynamic pricing: recalculated {} flower(s) and {} garland(s)", flowers.size(), garlands.size());
    }

    private double stockMultiplier(double quantity, List<String> reasons) {
        if (quantity > 100) {
            reasons.add("Surplus stock (" + (int) quantity + ") -12%");
            return 0.88;
        }
        if (quantity < 20) {
            reasons.add("Low stock (" + (int) quantity + ") +15%");
            return 1.15;
        }
        return 1.0;
    }

    private double festivalMultiplier(List<String> reasons) {
        LocalDateTime now = LocalDateTime.now();
        boolean festivalActive = festivalOfferRepository.findByActiveTrue().stream()
                .anyMatch(offer -> !now.isBefore(offer.getStartDate()) && !now.isAfter(offer.getEndDate()));
        if (festivalActive) {
            reasons.add("Festival offer active +10%");
            return 1.10;
        }
        return 1.0;
    }

    private double demandMultiplier(Integer orderCount, Integer viewCount, List<String> reasons) {
        int orders = orderCount == null ? 0 : orderCount;
        int views = viewCount == null ? 0 : viewCount;

        if (views < 5) {
            return 1.0; // not enough signal yet
        }

        double demandRatio = (double) orders / views;
        if (demandRatio > 0.30) {
            reasons.add("High demand (" + orders + " orders / " + views + " views) +8%");
            return 1.08;
        }
        if (demandRatio < 0.05) {
            reasons.add("Low demand (" + orders + " orders / " + views + " views) -8%");
            return 0.92;
        }
        return 1.0;
    }

    private double clamp(double multiplier) {
        return Math.max(MIN_MULTIPLIER, Math.min(MAX_MULTIPLIER, multiplier));
    }

    private void applyIfChanged(PricedItemType type, Long itemId, Double oldPrice, double newPrice, List<String> reasons) {
        if (oldPrice != null && Math.abs(oldPrice - newPrice) < 0.01) {
            return; // no meaningful change - skip noisy history rows
        }
        String reason = reasons.isEmpty() ? "Freshness markdown only" : String.join(" | ", reasons);
        priceHistoryRepository.save(PriceHistory.builder()
                .itemType(type)
                .itemId(itemId)
                .price(newPrice)
                .reason(reason)
                .build());
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
