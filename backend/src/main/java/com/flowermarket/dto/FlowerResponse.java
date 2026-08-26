package com.flowermarket.dto;

import com.flowermarket.enums.FlowerUnit;
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
public class FlowerResponse {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Long sellerId;
    private String shopName;
    private List<String> images;
    private Double basePrice;
    private Double currentPrice;   // dynamic price after freshness-based markdown
    private String priceStage;     // Fresh / 12h / 18h / 24h / Clearance
    private Double quantity;
    private FlowerUnit unit;
    private Integer freshnessHours;
    private LocalDateTime harvestDate;
    private LocalDateTime expiryDate;
    private boolean available;
    private boolean outOfStock;
    private Integer qualityScore;
    private String qualityVerdict;
    private LocalDateTime qualityCheckedAt;
}
