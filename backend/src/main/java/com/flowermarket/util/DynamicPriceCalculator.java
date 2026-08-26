package com.flowermarket.util;

import com.flowermarket.entity.Flower;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Unique Feature: Dynamic Pricing.
 * Flower price gradually decreases the older it gets, based on hours
 * elapsed since harvestDate relative to its freshnessHours window.
 *
 * Stages (relative to freshnessHours as the "fresh window"):
 *   0h                       -> 100% of basePrice   ("Fresh")
 *   freshnessHours * 0.5     -> ~92% of basePrice   ("12 Hours" style markdown)
 *   freshnessHours * 0.75    -> ~79% of basePrice   ("18 Hours")
 *   freshnessHours * 1.0     -> ~63% of basePrice   ("24 Hours")
 *   beyond freshnessHours    -> ~42% of basePrice   ("Clearance Sale")
 *   past expiryDate          -> flagged unavailable (should be waste-routed)
 */
@Component
public class DynamicPriceCalculator {

    public PriceResult calculate(Flower flower) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime harvest = flower.getHarvestDate();
        int freshnessHours = flower.getFreshnessHours() == null || flower.getFreshnessHours() <= 0
                ? 24 : flower.getFreshnessHours();

        long hoursElapsed = Duration.between(harvest, now).toHours();
        double ratio = (double) hoursElapsed / freshnessHours;

        double multiplier;
        String stage;

        if (ratio <= 0) {
            multiplier = 1.0;
            stage = "Fresh";
        } else if (ratio <= 0.5) {
            multiplier = 1.0 - (0.08 * (ratio / 0.5));       // 100% -> 92%
            stage = "Fresh";
        } else if (ratio <= 0.75) {
            multiplier = 0.92 - (0.13 * ((ratio - 0.5) / 0.25)); // 92% -> 79%
            stage = "12 Hours";
        } else if (ratio <= 1.0) {
            multiplier = 0.79 - (0.16 * ((ratio - 0.75) / 0.25)); // 79% -> 63%
            stage = "18 Hours";
        } else if (ratio <= 1.33) {
            multiplier = 0.63 - (0.21 * ((ratio - 1.0) / 0.33)); // 63% -> 42%
            stage = "24 Hours";
        } else {
            multiplier = 0.42;
            stage = "Clearance Sale";
        }

        multiplier = Math.max(multiplier, 0.30); // floor price at 30% of base
        double price = round2(flower.getBasePrice() * multiplier);

        boolean expired = flower.getExpiryDate() != null && now.isAfter(flower.getExpiryDate());

        return new PriceResult(price, stage, expired);
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    public record PriceResult(double price, String stage, boolean expired) {}
}
