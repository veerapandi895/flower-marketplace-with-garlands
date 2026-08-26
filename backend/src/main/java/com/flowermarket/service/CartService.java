package com.flowermarket.service;

import com.flowermarket.dto.CartItemRequest;
import com.flowermarket.entity.CartItem;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.User;
import com.flowermarket.enums.GarlandStatus;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final FlowerService flowerService;
    private final GarlandService garlandService;

    public CartItem addItem(User customer, CartItemRequest req) {
        if (req.getGarlandId() != null) {
            return addGarlandItem(customer, req);
        }
        return addFlowerItem(customer, req);
    }

    private CartItem addFlowerItem(User customer, CartItemRequest req) {
        Flower flower = flowerService.getById(req.getFlowerId());

        if (flower.isOutOfStock() || !flower.isAvailable()) {
            throw new BadRequestException("This flower is currently unavailable");
        }
        if (req.getQuantity() > flower.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

        double currentPrice = flowerService.getEffectivePrice(flower);

        return cartItemRepository.findByCustomerAndFlower(customer, flower)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                    existing.setPriceSnapshot(currentPrice);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(CartItem.builder()
                        .customer(customer)
                        .flower(flower)
                        .quantity(req.getQuantity())
                        .priceSnapshot(currentPrice)
                        .build()));
    }

    private CartItem addGarlandItem(User customer, CartItemRequest req) {
        Garland garland = garlandService.getById(req.getGarlandId());

        if (garland.getStatus() != GarlandStatus.ACTIVE) {
            throw new BadRequestException("This garland is currently unavailable");
        }
        if (req.getQuantity() > garland.getAvailableQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

        double currentPrice = garlandService.getEffectivePrice(garland);

        return cartItemRepository.findByCustomerAndGarland(customer, garland)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                    existing.setPriceSnapshot(currentPrice);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(CartItem.builder()
                        .customer(customer)
                        .garland(garland)
                        .quantity(req.getQuantity())
                        .priceSnapshot(currentPrice)
                        .build()));
    }

    public void removeItem(User customer, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BadRequestException("Cart item not found"));
        if (!item.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This cart item does not belong to you");
        }
        cartItemRepository.delete(item);
    }

    public CartItem updateQuantity(User customer, Long cartItemId, Double quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BadRequestException("Cart item not found"));
        if (!item.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This cart item does not belong to you");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void clearCart(User customer) {
        cartItemRepository.deleteByCustomer(customer);
    }

    public List<CartItem> getCart(User customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public double getCartTotal(User customer) {
        return getCart(customer).stream()
                .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                .sum();
    }
}
