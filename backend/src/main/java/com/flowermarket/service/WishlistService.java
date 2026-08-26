package com.flowermarket.service;

import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.User;
import com.flowermarket.entity.Wishlist;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final FlowerService flowerService;
    private final GarlandService garlandService;

    @Transactional
    public Wishlist add(User customer, Long flowerId) {

        if (wishlistRepository.existsByCustomerIdAndFlowerId(customer.getId(), flowerId)) {
            throw new BadRequestException("This flower is already in your wishlist");
        }

        Flower flower = flowerService.getById(flowerId);

        return wishlistRepository.save(
                Wishlist.builder()
                        .customer(customer)
                        .flower(flower)
                        .build()
        );
    }

    @Transactional
    public void remove(User customer, Long flowerId) {

        wishlistRepository.deleteByCustomerIdAndFlowerId(
                customer.getId(),
                flowerId
        );
    }

    @Transactional
    public Wishlist addGarland(User customer, Long garlandId) {

        if (wishlistRepository.existsByCustomerIdAndGarlandId(customer.getId(), garlandId)) {
            throw new BadRequestException("This garland is already in your wishlist");
        }

        Garland garland = garlandService.getById(garlandId);

        return wishlistRepository.save(
                Wishlist.builder()
                        .customer(customer)
                        .garland(garland)
                        .build()
        );
    }

    @Transactional
    public void removeGarland(User customer, Long garlandId) {

        wishlistRepository.deleteByCustomerIdAndGarlandId(
                customer.getId(),
                garlandId
        );
    }

    public List<Wishlist> getForCustomer(User customer) {

        return wishlistRepository.findByCustomer(customer);
    }
}