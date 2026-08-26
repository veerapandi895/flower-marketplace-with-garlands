package com.flowermarket.service;

import com.flowermarket.entity.Order;
import com.flowermarket.repository.OrderRepository;
import com.flowermarket.repository.WasteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final WasteRecordRepository wasteRecordRepository;
    private final WasteService wasteService;

    public Map<String, Object> dailySales(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByPlacedAtBetween(start, end);
        double revenue = orderRepository.sumRevenueBetween(start, end);
        return Map.of("date", date, "orderCount", orders.size(), "revenue", revenue, "orders", orders);
    }

    public Map<String, Object> monthlySales(YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
        List<Order> orders = orderRepository.findByPlacedAtBetween(start, end);
        double revenue = orderRepository.sumRevenueBetween(start, end);
        return Map.of("month", month.toString(), "orderCount", orders.size(), "revenue", revenue);
    }

    public Map<String, Object> yearlySales(int year) {
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year + 1, 1, 1).atStartOfDay();
        List<Order> orders = orderRepository.findByPlacedAtBetween(start, end);
        double revenue = orderRepository.sumRevenueBetween(start, end);
        return Map.of("year", year, "orderCount", orders.size(), "revenue", revenue);
    }

    public Map<String, Object> wasteReductionReport(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        return Map.of(
                "from", from,
                "to", to,
                "byDestination", wasteService.destinationSummary(start, end)
        );
    }
}
