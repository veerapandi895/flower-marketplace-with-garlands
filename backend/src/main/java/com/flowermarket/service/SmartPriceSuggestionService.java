package com.flowermarket.service;

import com.flowermarket.entity.Flower;
import org.springframework.stereotype.Service;

/**
 * Unique Feature: Smart Price Suggestion.
 * Suggests a selling price adjustment based on current stock level relative
 * to a "high stock" threshold, nudging sellers to move excess inventory faster.
 */
@Service
public class SmartPriceSuggestionService {

    private static final double HIGH_STOCK_THRESHOLD_KG = 500;
    private static final double VERY_HIGH_STOCK_THRESHOLD_KG = 1000;

    public String suggest(Flower flower) {
        double stock = flower.getQuantity();
        double base = flower.getBasePrice();

        if (stock >= VERY_HIGH_STOCK_THRESHOLD_KG) {
            double suggested = Math.round(base * 0.88 * 100) / 100.0;
            return String.format(
                    "Current stock is very high (%.0f %s). Recommended selling price ₹%.2f instead of ₹%.2f.",
                    stock, flower.getUnit(), suggested, base);
        } else if (stock >= HIGH_STOCK_THRESHOLD_KG) {
            double suggested = Math.round(base * 0.92 * 100) / 100.0;
            return String.format(
                    "Current stock is high (%.0f %s). Recommended selling price ₹%.2f instead of ₹%.2f.",
                    stock, flower.getUnit(), suggested, base);
        } else if (stock <= 10) {
            double suggested = Math.round(base * 1.05 * 100) / 100.0;
            return String.format(
                    "Stock is running low (%.0f %s). You could raise the price to ₹%.2f.",
                    stock, flower.getUnit(), suggested);
        }
        return "Stock levels are healthy. Current price of ₹" + base + " looks appropriate.";
    }
}
