package com.flowermarket.service;

import com.flowermarket.dto.ShopRequest;
import com.flowermarket.entity.Shop;
import com.flowermarket.entity.User;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public Shop createShop(User seller, ShopRequest req) {
        if (shopRepository.findBySeller(seller).isPresent()) {
            throw new BadRequestException("Shop already exists for this seller");
        }
        Shop shop = Shop.builder()
                .seller(seller)
                .shopName(req.getShopName())
                .logoUrl(req.getLogoUrl())
                .bannerUrl(req.getBannerUrl())
                .description(req.getDescription())
                .address(req.getAddress())
                .phone(req.getPhone())
                .email(req.getEmail())
                .gstNumber(req.getGstNumber())
                .deliveryRadiusKm(req.getDeliveryRadiusKm())
                .businessHours(req.getBusinessHours())
                .build();
        return shopRepository.save(shop);
    }

    public Shop updateShop(User seller, ShopRequest req) {
        Shop shop = getShopBySeller(seller);
        shop.setShopName(req.getShopName());
        if (req.getLogoUrl() != null) shop.setLogoUrl(req.getLogoUrl());
        if (req.getBannerUrl() != null) shop.setBannerUrl(req.getBannerUrl());
        shop.setDescription(req.getDescription());
        shop.setAddress(req.getAddress());
        shop.setPhone(req.getPhone());
        shop.setEmail(req.getEmail());
        shop.setGstNumber(req.getGstNumber());
        shop.setDeliveryRadiusKm(req.getDeliveryRadiusKm());
        shop.setBusinessHours(req.getBusinessHours());
        return shopRepository.save(shop);
    }

    public Shop getShopBySeller(User seller) {
        return shopRepository.findBySeller(seller)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found. Please create your shop first."));
    }

    public Shop getShopBySellerId(Long sellerId) {
        return shopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
    }

    public java.util.List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
}
