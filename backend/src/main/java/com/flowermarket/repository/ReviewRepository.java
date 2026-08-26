package com.flowermarket.repository;

import com.flowermarket.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFlowerId(Long flowerId);
    List<Review> findBySellerId(Long sellerId);

    // Phase 9: testimonials for the home page - well-rated reviews that have an actual comment.
    List<Review> findTop6ByRatingGreaterThanEqualAndCommentIsNotNullOrderByCreatedAtDesc(Integer minRating);
}
