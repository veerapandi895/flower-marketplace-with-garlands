package com.flowermarket.service;

import com.flowermarket.dto.FestivalOfferRequest;
import com.flowermarket.entity.FestivalOffer;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.FestivalOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalOfferService {

    private final FestivalOfferRepository festivalOfferRepository;

    public FestivalOffer create(FestivalOfferRequest req) {
        FestivalOffer offer = FestivalOffer.builder()
                .festivalType(req.getFestivalType())
                .title(req.getTitle())
                .description(req.getDescription())
                .discountPercent(req.getDiscountPercent())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();
        return festivalOfferRepository.save(offer);
    }

    public List<FestivalOffer> getActive() {
        LocalDateTime now = LocalDateTime.now();
        return festivalOfferRepository.findByActiveTrue().stream()
                .filter(o -> !now.isBefore(o.getStartDate()) && !now.isAfter(o.getEndDate()))
                .toList();
    }

    public List<FestivalOffer> getAll() {
        return festivalOfferRepository.findAll();
    }

    public void deactivate(Long id) {
        FestivalOffer offer = festivalOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Festival offer not found"));
        offer.setActive(false);
        festivalOfferRepository.save(offer);
    }
}
