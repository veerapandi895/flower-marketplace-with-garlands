package com.flowermarket.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String deliveryAddress;
    private String paymentMethod;
    private String couponCode;
}
