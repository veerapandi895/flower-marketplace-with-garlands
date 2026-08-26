package com.flowermarket.service;

import com.flowermarket.dto.ReviewRequest;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Review;
import com.flowermarket.entity.Shop;
import com.flowermarket.entity.User;
import com.flowermarket.repository.ReviewRepository;
import com.flowermarket.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FlowerService flowerService;
    private final ShopRepository shopRepository;

    public Review addReview(User customer, ReviewRequest req) {
        Flower flower = req.getFlowerId() != null ? flowerService.getById(req.getFlowerId()) : null;

        Review review = Review.builder()
                .customer(customer)
                .flower(flower)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        if (req.getSellerId() != null) {
            review.setSeller(flower != null ? flower.getSeller() : null);
            recalculateSellerRating(req.getSellerId());
        }

        return reviewRepository.save(review);
    }

    private void recalculateSellerRating(Long sellerId) {
        List<Review> reviews = reviewRepository.findBySellerId(sellerId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        shopRepository.findBySellerId(sellerId).ifPresent(shop -> {
            shop.setRating(Math.round(avg * 10.0) / 10.0);
            shopRepository.save(shop);
        });
    }

    public List<Review> getFlowerReviews(Long flowerId) {
        return reviewRepository.findByFlowerId(flowerId);
    }

    public List<Review> getSellerReviews(Long sellerId) {
        return reviewRepository.findBySellerId(sellerId);
    }

    // Phase 9: home page testimonials.
    public List<Review> getTestimonials() {
        return reviewRepository.findTop6ByRatingGreaterThanEqualAndCommentIsNotNullOrderByCreatedAtDesc(4);
    }
}
