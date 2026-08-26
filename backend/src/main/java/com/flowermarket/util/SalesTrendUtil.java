package com.flowermarket.util;

import com.flowermarket.entity.Order;
import com.flowermarket.enums.OrderStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a 6-month revenue + order-count trend from a list of orders.
 * Shared by SellerDashboardService (per-seller) and AdminDashboardService
 * (platform-wide) so the aggregation logic lives in exactly one place.
 */
public final class SalesTrendUtil {

    private static final DateTimeFormatter MONTH_KEY_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_LABEL_FMT = DateTimeFormatter.ofPattern("MMM yyyy");

    private SalesTrendUtil() {
    }

    public static List<Map<String, Object>> lastSixMonths(List<Order> orders) {
        Map<String, Map<String, Object>> byMonth = new LinkedHashMap<>();
        LocalDate cursor = LocalDate.now().withDayOfMonth(1).minusMonths(5);
        for (int i = 0; i < 6; i++) {
            String key = cursor.format(MONTH_KEY_FMT);
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", cursor.format(MONTH_LABEL_FMT));
            entry.put("revenue", 0.0);
            entry.put("orders", 0);
            byMonth.put(key, entry);
            cursor = cursor.plusMonths(1);
        }

        for (Order order : orders) {
            String key = order.getPlacedAt().format(MONTH_KEY_FMT);
            Map<String, Object> entry = byMonth.get(key);
            if (entry == null) continue; // outside the 6-month window
            entry.put("orders", (int) entry.get("orders") + 1);
            if (order.getStatus() == OrderStatus.COMPLETED) {
                entry.put("revenue", (double) entry.get("revenue") + order.getTotalAmount());
            }
        }

        return new ArrayList<>(byMonth.values());
    }
}
