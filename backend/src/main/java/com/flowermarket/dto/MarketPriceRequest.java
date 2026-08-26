package com.flowermarket.dto;

import lombok.Data;

@Data
public class MarketPriceRequest {
    private String flowerName;
    private Double pricePerKg;
}
