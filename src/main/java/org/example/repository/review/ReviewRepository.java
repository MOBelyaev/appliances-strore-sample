package org.example.repository.review;

import org.example.model.review.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    long count();
    Optional<Review> findById(long id);
    long countReviewForProduct(long productId);
    List<Review> findReviewForProduct(long productId, int page, int pageSize);
    long countCommentsForReview(long reviewId);
    List<Review> findCommentsForReview(long reviewId, int page, int pageSize);
    void saveReview(Review review);
    void saveComment(long reviewId, Review comment);
}
