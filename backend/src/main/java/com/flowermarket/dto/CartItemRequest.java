package com.flowermarket.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    // Set exactly one of flowerId / garlandId
    private Long flowerId;
    private Long garlandId;
    private Double quantity;
}
