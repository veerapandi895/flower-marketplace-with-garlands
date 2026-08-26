package com.flowermarket.service;

import com.flowermarket.dto.WasteRecordRequest;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.User;
import com.flowermarket.entity.WasteRecord;
import com.flowermarket.repository.FlowerRepository;
import com.flowermarket.repository.WasteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unique Feature: Waste Reduction.
 * Expired / unsold flowers get routed to Temple Donation, NGO, Compost,
 * Incense Stick Factory, or Natural Color Makers instead of being discarded.
 */
@Service
@RequiredArgsConstructor
public class WasteService {

    private final WasteRecordRepository wasteRecordRepository;
    private final FlowerRepository flowerRepository;

    public WasteRecord recordWaste(User seller, WasteRecordRequest req) {
        Flower flower = flowerRepository.findById(req.getFlowerId())
                .orElseThrow(() -> new com.flowermarket.exception.ResourceNotFoundException("Flower not found"));

        WasteRecord record = WasteRecord.builder()
                .flower(flower)
                .seller(seller)
                .quantity(req.getQuantity())
                .destination(req.getDestination())
                .notes(req.getNotes())
                .build();

        flower.setQuantity(Math.max(0, flower.getQuantity() - req.getQuantity()));
        if (flower.getQuantity() <= 0) {
            flower.setOutOfStock(true);
            flower.setAvailable(false);
        }
        flowerRepository.save(flower);

        return wasteRecordRepository.save(record);
    }

    public Map<String, Double> destinationSummary(LocalDateTime start, LocalDateTime end) {
        List<WasteRecord> records = wasteRecordRepository.findByRecordedAtBetween(start, end);
        return records.stream().collect(Collectors.groupingBy(
                r -> r.getDestination().name(),
                Collectors.summingDouble(WasteRecord::getQuantity)));
    }
}
