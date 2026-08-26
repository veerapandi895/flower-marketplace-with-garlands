package com.flowermarket.dto;

import lombok.Data;

@Data
public class ShopRequest {
    private String shopName;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String gstNumber;
    private Double deliveryRadiusKm;
    private String businessHours;
}
