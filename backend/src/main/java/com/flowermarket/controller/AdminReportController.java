package com.flowermarket.controller;

import com.flowermarket.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.dailySales(date));
    }

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> monthly(@RequestParam String month) {
        return ResponseEntity.ok(reportService.monthlySales(YearMonth.parse(month)));
    }

    @GetMapping("/yearly")
    public ResponseEntity<Map<String, Object>> yearly(@RequestParam int year) {
        return ResponseEntity.ok(reportService.yearlySales(year));
    }

    @GetMapping("/waste-reduction")
    public ResponseEntity<Map<String, Object>> wasteReduction(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.wasteReductionReport(from, to));
    }
}
