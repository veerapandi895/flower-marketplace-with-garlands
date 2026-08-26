package com.flowermarket.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long flowerId;
    private Long sellerId;
    private Integer rating;
    private String comment;
}
