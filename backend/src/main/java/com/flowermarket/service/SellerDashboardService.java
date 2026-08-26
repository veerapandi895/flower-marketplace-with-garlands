package com.flowermarket.service;

import com.flowermarket.entity.Order;
import com.flowermarket.entity.User;
import com.flowermarket.enums.OrderStatus;
import com.flowermarket.repository.FlowerRepository;
import com.flowermarket.repository.GarlandRepository;
import com.flowermarket.repository.OrderRepository;
import com.flowermarket.util.SalesTrendUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerDashboardService {

    private static final Set<OrderStatus> PENDING_STATUSES =
            Set.of(OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.PACKED, OrderStatus.READY);

    private final OrderRepository orderRepository;
    private final FlowerRepository flowerRepository;
    private final GarlandRepository garlandRepository;

    public Map<String, Object> getSummary(User seller) {
        Map<String, Object> summary = new HashMap<>();

        List<Order> allOrders = orderRepository.findBySeller(seller);
        summary.put("totalRevenue", orderRepository.sumRevenueBySeller(seller.getId()));
        summary.put("totalOrders", allOrders.size());

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long todayOrders = allOrders.stream().filter(o -> o.getPlacedAt().isAfter(startOfToday)).count();
        summary.put("todaysOrders", todayOrders);

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthlyOrders = allOrders.stream().filter(o -> o.getPlacedAt().isAfter(startOfMonth)).count();
        summary.put("monthlyOrders", monthlyOrders);

        long cancelled = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REJECTED)
                .count();
        summary.put("cancelledOrders", cancelled);

        long pendingCount = allOrders.stream().filter(o -> PENDING_STATUSES.contains(o.getStatus())).count();
        summary.put("pendingOrders", pendingCount);

        summary.put("bestSellingFlower", flowerRepository.findBySellerId(seller.getId()).stream()
                .max(Comparator.comparing(f -> f.getOrderCount() == null ? 0 : f.getOrderCount()))
                .map(f -> f.getName())
                .orElse("N/A"));

        summary.put("bestSellingGarland", garlandRepository.findBySellerId(seller.getId()).stream()
                .max(Comparator.comparing(g -> g.getOrderCount() == null ? 0 : g.getOrderCount()))
                .map(g -> g.getName())
                .orElse("N/A"));

        summary.put("lowStockFlowers", flowerRepository.findLowStockBySeller(seller.getId(), 10.0));
        summary.put("lowStockGarlands", garlandRepository.findLowStockBySeller(seller.getId(), 10));

        // Phase 10: 6-month revenue + order-count trend, ready for a bar/line chart.
        summary.put("monthlySales", SalesTrendUtil.lastSixMonths(allOrders));

        // Recent orders (most recent 5) for the dashboard feed.
        summary.put("recentOrders", allOrders.stream()
                .sorted(Comparator.comparing(Order::getPlacedAt).reversed())
                .limit(5)
                .map(this::toOrderSummary)
                .toList());

        return summary;
    }

    private Map<String, Object> toOrderSummary(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("customerName", order.getCustomer().getName());
        map.put("totalAmount", order.getTotalAmount());
        map.put("status", order.getStatus());
        map.put("placedAt", order.getPlacedAt());
        map.put("itemCount", order.getItems().size());
        return map;
    }
}
