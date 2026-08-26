package com.flowermarket.dto;

import com.flowermarket.enums.FlowerUnit;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlowerRequest {
    private String name;
    private String description;
    private Long categoryId;
    private List<String> images;
    private Double basePrice;
    private Double quantity;
    private FlowerUnit unit;
    private Integer freshnessHours;
    private LocalDateTime harvestDate;
    private LocalDateTime expiryDate;
}
