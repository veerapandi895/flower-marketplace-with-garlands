package com.flowermarket.service;

import com.flowermarket.dto.FlowerRequest;
import com.flowermarket.dto.FlowerResponse;
import com.flowermarket.entity.Category;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Shop;
import com.flowermarket.entity.User;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.CategoryRepository;
import com.flowermarket.repository.FlowerRepository;
import com.flowermarket.repository.ShopRepository;
import com.flowermarket.util.DynamicPriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlowerService {

    private final FlowerRepository flowerRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final DynamicPriceCalculator priceCalculator;
    private final AIQualityCheckService aiQualityCheckService;
    private final DynamicPricingService dynamicPricingService;

    /** The price actually charged: dynamic-pricing result once computed, else the live freshness price. */
    public double getEffectivePrice(Flower flower) {
        return flower.getCurrentPrice() != null ? flower.getCurrentPrice() : priceCalculator.calculate(flower).price();
    }

    public Flower addFlower(User seller, FlowerRequest req) {
        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Flower flower = Flower.builder()
                .name(req.getName())
                .description(req.getDescription())
                .category(category)
                .seller(seller)
                .images(req.getImages())
                .basePrice(req.getBasePrice())
                .quantity(req.getQuantity())
                .unit(req.getUnit())
                .freshnessHours(req.getFreshnessHours())
                .harvestDate(req.getHarvestDate())
                .expiryDate(req.getExpiryDate())
                .build();

        Flower saved = flowerRepository.save(flower);
        dynamicPricingService.recalculateFlowerPrice(saved);
        return flowerRepository.save(saved);
    }

    public Flower updateFlower(User seller, Long flowerId, FlowerRequest req) {
        Flower flower = getOwnedFlower(seller, flowerId);

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            flower.setCategory(category);
        }
        flower.setName(req.getName());
        flower.setDescription(req.getDescription());
        if (req.getImages() != null) flower.setImages(req.getImages());
        flower.setBasePrice(req.getBasePrice());
        flower.setQuantity(req.getQuantity());
        flower.setUnit(req.getUnit());
        flower.setFreshnessHours(req.getFreshnessHours());
        flower.setHarvestDate(req.getHarvestDate());
        flower.setExpiryDate(req.getExpiryDate());

        Flower saved = flowerRepository.save(flower);
        dynamicPricingService.recalculateFlowerPrice(saved);
        return flowerRepository.save(saved);
    }

    public void deleteFlower(User seller, Long flowerId) {
        Flower flower = getOwnedFlower(seller, flowerId);
        flowerRepository.delete(flower);
    }

    public Flower markOutOfStock(User seller, Long flowerId, boolean outOfStock) {
        Flower flower = getOwnedFlower(seller, flowerId);
        flower.setOutOfStock(outOfStock);
        Flower saved = flowerRepository.save(flower);
        dynamicPricingService.recalculateFlowerPrice(saved);
        return flowerRepository.save(saved);
    }

    public Flower setQuantity(User seller, Long flowerId, Double quantity) {
        Flower flower = getOwnedFlower(seller, flowerId);
        flower.setQuantity(quantity);
        if (quantity <= 0) flower.setOutOfStock(true);
        Flower saved = flowerRepository.save(flower);
        dynamicPricingService.recalculateFlowerPrice(saved);
        return flowerRepository.save(saved);
    }

    private Flower getOwnedFlower(User seller, Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Flower not found"));
        if (!flower.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("You do not own this flower listing");
        }
        return flower;
    }

    public List<Flower> getSellerFlowers(Long sellerId) {
        return flowerRepository.findBySellerId(sellerId);
    }

    public List<Flower> getAvailableSellerFlowers(Long sellerId) {
        return flowerRepository.findBySellerIdAndAvailableTrueAndOutOfStockFalse(sellerId);
    }

    public List<Flower> getAllAvailable() {
        return flowerRepository.findByAvailableTrueAndOutOfStockFalse();
    }

    /** Phase 8: combined filter (category/price/seller) + sort, applied over the live available set. */
    public List<Flower> filter(Long categoryId, Double minPrice, Double maxPrice, Long sellerId, String sort) {
        List<Flower> base = categoryId != null ? byCategory(categoryId) : getAllAvailable();

        List<Flower> filtered = base.stream()
                .filter(f -> sellerId == null || f.getSeller().getId().equals(sellerId))
                .filter(f -> minPrice == null || getEffectivePrice(f) >= minPrice)
                .filter(f -> maxPrice == null || getEffectivePrice(f) <= maxPrice)
                .toList();

        if (sort == null) return filtered;
        return switch (sort) {
            case "price_asc" -> filtered.stream().sorted(java.util.Comparator.comparingDouble(this::getEffectivePrice)).toList();
            case "price_desc" -> filtered.stream().sorted(java.util.Comparator.comparingDouble(this::getEffectivePrice).reversed()).toList();
            case "newest" -> filtered.stream().sorted(java.util.Comparator.comparing(Flower::getCreatedAt).reversed()).toList();
            case "popular" -> filtered.stream().sorted(java.util.Comparator.comparing(Flower::getOrderCount, java.util.Comparator.nullsFirst(Integer::compareTo)).reversed()).toList();
            default -> filtered;
        };
    }

    public List<Flower> search(String query) {
        return flowerRepository.searchByName(query);
    }

    public List<Flower> byCategory(Long categoryId) {
        return flowerRepository.findByCategoryId(categoryId);
    }

    public List<Flower> trending() {
        return flowerRepository.findTop10ByOrderByOrderCountDesc();
    }

    public List<Flower> mostViewed() {
        return flowerRepository.findTop10ByOrderByViewCountDesc();
    }

    public List<Flower> lowStockForSeller(Long sellerId, double threshold) {
        return flowerRepository.findLowStockBySeller(sellerId, threshold);
    }

    public List<Flower> lowStockAll(double threshold) {
        return flowerRepository.findAllLowStock(threshold);
    }

    public Flower incrementView(Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Flower not found"));
        flower.setViewCount((flower.getViewCount() == null ? 0 : flower.getViewCount()) + 1);
        return flowerRepository.save(flower);
    }

    /** Converts entity + live dynamic-price calculation into the response DTO. */
    public FlowerResponse toResponse(Flower flower) {
        DynamicPriceCalculator.PriceResult priceResult = priceCalculator.calculate(flower);
        Optional<Shop> shop = shopRepository.findBySellerId(flower.getSeller().getId());

        return FlowerResponse.builder()
                .id(flower.getId())
                .name(flower.getName())
                .description(flower.getDescription())
                .categoryName(flower.getCategory() != null ? flower.getCategory().getName() : null)
                .sellerId(flower.getSeller().getId())
                .shopName(shop.map(Shop::getShopName).orElse(flower.getSeller().getName()))
                .images(flower.getImages())
                .basePrice(flower.getBasePrice())
                .currentPrice(getEffectivePrice(flower))
                .priceStage(priceResult.stage())
                .quantity(flower.getQuantity())
                .unit(flower.getUnit())
                .freshnessHours(flower.getFreshnessHours())
                .harvestDate(flower.getHarvestDate())
                .expiryDate(flower.getExpiryDate())
                .available(flower.isAvailable() && !priceResult.expired())
                .outOfStock(flower.isOutOfStock())
                .qualityScore(flower.getQualityScore())
                .qualityVerdict(flower.getQualityVerdict())
                .qualityCheckedAt(flower.getQualityCheckedAt())
                .build();
    }

    public Flower runQualityCheck(User seller, Long flowerId) {
        Flower flower = getOwnedFlower(seller, flowerId);
        AIQualityCheckService.QualityResult result = aiQualityCheckService.check(flower);
        flower.setQualityScore(result.score());
        flower.setQualityVerdict(result.verdict());
        flower.setQualityCheckedAt(LocalDateTime.now());
        return flowerRepository.save(flower);
    }

    public Flower getById(Long id) {
        return flowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flower not found"));
    }
}
