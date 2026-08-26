package com.flowermarket.dto;

import com.flowermarket.enums.GarlandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarlandResponse {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Long categoryId;
    private Long sellerId;
    private String shopName;
    private List<String> images;
    private Double price;          // original/seller-set price
    private Double currentPrice;   // live dynamic-pricing price (Phase 5)
    private Double weightGrams;
    private Integer availableQuantity;
    private String flowerComposition;
    private Integer estimatedPrepMinutes;
    private GarlandStatus status;
    private Integer viewCount;
    private Integer orderCount;
    private LocalDateTime createdAt;
}
