package org.example.service;

import org.example.model.review.Review;

import java.util.List;

public interface ReviewService {
    Review getById(long id);
    long getCountReviewForProduct(long productId);
    List<Review> getReviewForProduct(long productId, int page, int pageSize);
    long getCountCommentsForReview(long reviewId);
    List<Review> getCommentsForReview(long reviewId, int page, int pageSize);
    void saveReview(Review review);
    void saveComment(long reviewId, Review comment);
}
