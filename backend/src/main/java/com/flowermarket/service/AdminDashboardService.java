package com.flowermarket.service;

import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.Order;
import com.flowermarket.entity.Shop;
import com.flowermarket.enums.OrderStatus;
import com.flowermarket.enums.Role;
import com.flowermarket.repository.*;
import com.flowermarket.util.SalesTrendUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FlowerRepository flowerRepository;
    private final GarlandRepository garlandRepository;
    private final CouponRepository couponRepository;
    private final FestivalOfferRepository festivalOfferRepository;
    private final ShopRepository shopRepository;

    public Map<String, Object> getSummary() {

        Map<String, Object> summary = new HashMap<>();

        summary.put("totalUsers", userRepository.count());
        summary.put("totalSellers", userRepository.countByRole(Role.SELLER));
        summary.put("totalCustomers", userRepository.countByRole(Role.CUSTOMER));
        summary.put("totalFlowers", flowerRepository.count());
        summary.put("totalGarlands", garlandRepository.count());
        summary.put("totalCoupons", couponRepository.count());
        summary.put("totalFestivalOffers", festivalOfferRepository.count());

        summary.put("totalOrders", orderRepository.count());
        summary.put("completedOrders",
                orderRepository.countByStatus(OrderStatus.COMPLETED));

        LocalDateTime startOfMonth =
                LocalDate.now().withDayOfMonth(1).atStartOfDay();

        summary.put(
                "monthlyRevenue",
                orderRepository.sumRevenueBetween(
                        startOfMonth,
                        LocalDateTime.now()
                )
        );

        List<Flower> topSellingFlowers =
                flowerRepository.findTop10ByOrderByOrderCountDesc();

        summary.put("topSellingFlowers", topSellingFlowers);

        List<Garland> topSellingGarlands =
                garlandRepository.findTop10ByOrderByOrderCountDesc();

        summary.put("topSellingGarlands", topSellingGarlands);

        List<Flower> lowStockFlowers =
                flowerRepository.findAllLowStock(10.0);

        summary.put("lowStockFlowers", lowStockFlowers);

        summary.put(
                "orderStatusBreakdown",
                buildOrderStatusBreakdown()
        );

        LocalDateTime windowStart =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .minusMonths(5)
                        .atStartOfDay();

        List<Order> recentOrders =
                orderRepository.findByPlacedAtBetween(
                        windowStart,
                        LocalDateTime.now()
                );

        summary.put(
                "monthlySales",
                SalesTrendUtil.lastSixMonths(recentOrders)
        );

        summary.put(
                "topSellers",
                buildTopSellers()
        );

        summary.put(
                "wasteReduction",
                buildWasteReductionStats()
        );

        return summary;
    }

    private List<Map<String, Object>> buildOrderStatusBreakdown() {

        List<Map<String, Object>> breakdown = new ArrayList<>();

        for (OrderStatus status : OrderStatus.values()) {

            Map<String, Object> map = new HashMap<>();

            map.put("status", status.name());
            map.put("count", orderRepository.countByStatus(status));

            breakdown.add(map);
        }

        return breakdown;
    }

    private List<Map<String, Object>> buildTopSellers() {

        List<Shop> shops = shopRepository.findAll();

        return shops.stream()
                .map(shop -> {
                    Double revenue = orderRepository.sumRevenueBySeller(shop.getSeller().getId());

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("sellerId", shop.getSeller().getId());
                    entry.put("shopName", shop.getShopName());
                    entry.put("revenue", revenue == null ? 0.0 : revenue);
                    entry.put("rating", shop.getRating());

                    return entry;
                })
                .sorted(
                        Comparator.comparing(
                                (Map<String, Object> e) -> (Double) e.get("revenue")
                        ).reversed()
                )
                .limit(10)
                .toList();
    }
    private Map<String, Object> buildWasteReductionStats() {

        List<Flower> flowers = flowerRepository.findAll();

        long totalListed = flowers.size();

        long soldBeforeWaste =
                flowers.stream()
                        .filter(f ->
                                f.getOrderCount() != null &&
                                f.getOrderCount() > 0
                        )
                        .count();

        long expiredUnsold =
                flowers.stream()
                        .filter(f -> !f.isAvailable())
                        .filter(f ->
                                f.getOrderCount() == null ||
                                f.getOrderCount() == 0
                        )
                        .count();

        double wasteReductionPercent =
                totalListed == 0
                        ? 0.0
                        : (soldBeforeWaste * 100.0 / totalListed);

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalListed", totalListed);
        stats.put("soldBeforeWaste", soldBeforeWaste);
        stats.put("expiredUnsold", expiredUnsold);
        stats.put(
                "wasteReductionPercent",
                Math.round(wasteReductionPercent * 10.0) / 10.0
        );

        return stats;
    }
}