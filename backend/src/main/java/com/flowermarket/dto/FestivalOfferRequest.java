package com.flowermarket.dto;

import com.flowermarket.enums.FestivalType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FestivalOfferRequest {
    private FestivalType festivalType;
    private String title;
    private String description;
    private Double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
