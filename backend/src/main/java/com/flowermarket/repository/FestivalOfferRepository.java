package com.flowermarket.repository;

import com.flowermarket.entity.FestivalOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FestivalOfferRepository extends JpaRepository<FestivalOffer, Long> {
    List<FestivalOffer> findByActiveTrue();
}
