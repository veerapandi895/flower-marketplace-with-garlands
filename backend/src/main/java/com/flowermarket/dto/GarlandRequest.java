package com.flowermarket.dto;

import lombok.Data;

import java.util.List;

@Data
public class GarlandRequest {
    private String name;
    private String description;
    private Long categoryId;
    private List<String> images;
    private Double price;
    private Double weightGrams;
    private Integer availableQuantity;
    private String flowerComposition;
    private Integer estimatedPrepMinutes;
}
