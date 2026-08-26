package com.flowermarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DynamicPricingScheduler {

    private final DynamicPricingService dynamicPricingService;

    // Runs every 15 minutes.
    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void recalculatePrices() {
        dynamicPricingService.recalculateAll();
    }
}
