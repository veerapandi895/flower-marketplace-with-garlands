package com.flowermarket.service;

import com.flowermarket.entity.Flower;
import com.flowermarket.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 4: Auto-delete/expire flowers.
 * Flowers are only fresh for a limited window (freshnessHours, default 24h)
 * measured from harvestDate, or an explicit expiryDate if the seller set one.
 * Once expired, a flower must stop appearing anywhere a customer browses
 * (home, search, category, wishlist listings still work but show as expired,
 * checkout is blocked) — enforced by flipping `available` to false so every
 * repository query that already filters on `available = true` excludes it.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlowerExpiryScheduler {

    private final FlowerRepository flowerRepository;

    // Runs every 5 minutes.
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void expireStaleFlowers() {
        LocalDateTime now = LocalDateTime.now();

        // 1) Flowers with an explicit expiryDate that has passed.
        List<Flower> explicitlyExpired = flowerRepository.findExpiredButStillAvailable(now);
        for (Flower flower : explicitlyExpired) {
            flower.setAvailable(false);
        }
        if (!explicitlyExpired.isEmpty()) {
            flowerRepository.saveAll(explicitlyExpired);
        }

        // 2) Flowers with no expiryDate: fall back to harvestDate + freshnessHours (default 24h).
        List<Flower> all = flowerRepository.findAll();
        List<Flower> implicitlyExpired = all.stream()
                .filter(f -> f.isAvailable() && f.getExpiryDate() == null && f.getHarvestDate() != null)
                .filter(f -> {
                    int freshnessHours = (f.getFreshnessHours() == null || f.getFreshnessHours() <= 0)
                            ? 24 : f.getFreshnessHours();
                    return f.getHarvestDate().plusHours(freshnessHours).isBefore(now);
                })
                .toList();
        for (Flower flower : implicitlyExpired) {
            flower.setAvailable(false);
        }
        if (!implicitlyExpired.isEmpty()) {
            flowerRepository.saveAll(implicitlyExpired);
        }

        int total = explicitlyExpired.size() + implicitlyExpired.size();
        if (total > 0) {
            log.info("Flower expiry job: marked {} flower(s) as expired/unavailable", total);
        }
    }
}
