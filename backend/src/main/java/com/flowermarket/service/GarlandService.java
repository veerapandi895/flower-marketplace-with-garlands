package com.flowermarket.service;

import com.flowermarket.dto.GarlandRequest;
import com.flowermarket.dto.GarlandResponse;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.GarlandCategory;
import com.flowermarket.entity.Shop;
import com.flowermarket.entity.User;
import com.flowermarket.enums.GarlandStatus;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.GarlandCategoryRepository;
import com.flowermarket.repository.GarlandRepository;
import com.flowermarket.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GarlandService {

    private final GarlandRepository garlandRepository;
    private final GarlandCategoryRepository garlandCategoryRepository;
    private final ShopRepository shopRepository;
    private final DynamicPricingService dynamicPricingService;

    /** The price actually charged: dynamic-pricing result once computed, else the seller-set price. */
    public double getEffectivePrice(Garland garland) {
        return garland.getCurrentPrice() != null ? garland.getCurrentPrice() : garland.getPrice();
    }

    public Garland addGarland(User seller, GarlandRequest req) {
        GarlandCategory category = resolveCategory(req.getCategoryId());

        Garland garland = Garland.builder()
                .name(req.getName())
                .description(req.getDescription())
                .category(category)
                .seller(seller)
                .images(req.getImages())
                .price(req.getPrice())
                .weightGrams(req.getWeightGrams())
                .availableQuantity(req.getAvailableQuantity())
                .flowerComposition(req.getFlowerComposition())
                .estimatedPrepMinutes(req.getEstimatedPrepMinutes())
                .status(deriveStatus(req.getAvailableQuantity(), null))
                .build();

        Garland saved = garlandRepository.save(garland);
        dynamicPricingService.recalculateGarlandPrice(saved);
        return garlandRepository.save(saved);
    }

    public Garland updateGarland(User seller, Long garlandId, GarlandRequest req) {
        Garland garland = getOwnedGarland(seller, garlandId);

        garland.setCategory(resolveCategory(req.getCategoryId()));
        garland.setName(req.getName());
        garland.setDescription(req.getDescription());
        if (req.getImages() != null) garland.setImages(req.getImages());
        garland.setPrice(req.getPrice());
        garland.setWeightGrams(req.getWeightGrams());
        garland.setAvailableQuantity(req.getAvailableQuantity());
        garland.setFlowerComposition(req.getFlowerComposition());
        garland.setEstimatedPrepMinutes(req.getEstimatedPrepMinutes());
        garland.setStatus(deriveStatus(req.getAvailableQuantity(), garland.getStatus()));

        Garland saved = garlandRepository.save(garland);
        dynamicPricingService.recalculateGarlandPrice(saved);
        return garlandRepository.save(saved);
    }

    public void deleteGarland(User seller, Long garlandId) {
        Garland garland = getOwnedGarland(seller, garlandId);
        garlandRepository.delete(garland);
    }

    public Garland setStock(User seller, Long garlandId, Integer quantity) {
        Garland garland = getOwnedGarland(seller, garlandId);
        garland.setAvailableQuantity(quantity);
        garland.setStatus(deriveStatus(quantity, garland.getStatus()));
        Garland saved = garlandRepository.save(garland);
        dynamicPricingService.recalculateGarlandPrice(saved);
        return garlandRepository.save(saved);
    }

    public Garland setActive(User seller, Long garlandId, boolean active) {
        Garland garland = getOwnedGarland(seller, garlandId);
        if (!active) {
            garland.setStatus(GarlandStatus.INACTIVE);
        } else {
            garland.setStatus(deriveStatus(garland.getAvailableQuantity(), null));
        }
        Garland saved = garlandRepository.save(garland);
        dynamicPricingService.recalculateGarlandPrice(saved);
        return garlandRepository.save(saved);
    }

    private GarlandStatus deriveStatus(Integer quantity, GarlandStatus current) {
        if (current == GarlandStatus.INACTIVE) return GarlandStatus.INACTIVE;
        if (quantity == null || quantity <= 0) return GarlandStatus.OUT_OF_STOCK;
        return GarlandStatus.ACTIVE;
    }

    private GarlandCategory resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return garlandCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Garland category not found"));
    }

    private Garland getOwnedGarland(User seller, Long garlandId) {
        Garland garland = garlandRepository.findById(garlandId)
                .orElseThrow(() -> new ResourceNotFoundException("Garland not found"));
        if (!garland.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("You do not own this garland listing");
        }
        return garland;
    }

    public List<Garland> getSellerGarlands(Long sellerId) {
        return garlandRepository.findBySellerId(sellerId);
    }

    public List<Garland> getAvailableSellerGarlands(Long sellerId) {
        return garlandRepository.findBySellerIdAndStatus(sellerId, GarlandStatus.ACTIVE);
    }

    public List<Garland> getAllAvailable() {
        return garlandRepository.findByStatus(GarlandStatus.ACTIVE);
    }

    /** Phase 8: combined filter (category/price/seller) + sort, applied over the live active set. */
    public List<Garland> filter(Long categoryId, Double minPrice, Double maxPrice, Long sellerId, String sortBy) {
        List<Garland> base = categoryId != null ? byCategory(categoryId) : getAllAvailable();

        List<Garland> filtered = base.stream()
                .filter(g -> g.getStatus() == GarlandStatus.ACTIVE)
                .filter(g -> sellerId == null || g.getSeller().getId().equals(sellerId))
                .filter(g -> minPrice == null || getEffectivePrice(g) >= minPrice)
                .filter(g -> maxPrice == null || getEffectivePrice(g) <= maxPrice)
                .toList();

        return sort(filtered, sortBy);
    }

    public List<Garland> search(String query) {
        return garlandRepository.searchByName(query);
    }

    public List<Garland> byCategory(Long categoryId) {
        return garlandRepository.findByCategoryId(categoryId);
    }

    public List<Garland> sort(List<Garland> garlands, String sortBy) {
        if (sortBy == null) return garlands;
        return switch (sortBy) {
            case "price_asc" -> garlands.stream().sorted(Comparator.comparingDouble(this::getEffectivePrice)).toList();
            case "price_desc" -> garlands.stream().sorted(Comparator.comparingDouble(this::getEffectivePrice).reversed()).toList();
            case "newest" -> garlands.stream().sorted(Comparator.comparing(Garland::getCreatedAt).reversed()).toList();
            case "popular" -> garlands.stream().sorted(Comparator.comparing(Garland::getOrderCount).reversed()).toList();
            default -> garlands;
        };
    }

    public List<Garland> trending() {
        return garlandRepository.findTop10ByOrderByOrderCountDesc();
    }

    public List<Garland> lowStockForSeller(Long sellerId, int threshold) {
        return garlandRepository.findLowStockBySeller(sellerId, threshold);
    }

    public Garland incrementView(Long garlandId) {
        Garland garland = getById(garlandId);
        garland.setViewCount((garland.getViewCount() == null ? 0 : garland.getViewCount()) + 1);
        return garlandRepository.save(garland);
    }

    public Garland getById(Long id) {
        return garlandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garland not found"));
    }

    public GarlandResponse toResponse(Garland garland) {
        Optional<Shop> shop = shopRepository.findBySellerId(garland.getSeller().getId());
        return GarlandResponse.builder()
                .id(garland.getId())
                .name(garland.getName())
                .description(garland.getDescription())
                .categoryName(garland.getCategory() != null ? garland.getCategory().getName() : null)
                .categoryId(garland.getCategory() != null ? garland.getCategory().getId() : null)
                .sellerId(garland.getSeller().getId())
                .shopName(shop.map(Shop::getShopName).orElse(garland.getSeller().getName()))
                .images(garland.getImages())
                .price(garland.getPrice())
                .currentPrice(getEffectivePrice(garland))
                .weightGrams(garland.getWeightGrams())
                .availableQuantity(garland.getAvailableQuantity())
                .flowerComposition(garland.getFlowerComposition())
                .estimatedPrepMinutes(garland.getEstimatedPrepMinutes())
                .status(garland.getStatus())
                .viewCount(garland.getViewCount())
                .orderCount(garland.getOrderCount())
                .createdAt(garland.getCreatedAt())
                .build();
    }
}
