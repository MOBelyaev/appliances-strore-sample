package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.exeption.EntityNotFoundException;
import org.example.model.review.Review;
import org.example.repository.review.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReviewServiceV1 implements ReviewService {

    private final ReviewRepository repository;

    public ReviewServiceV1(ReviewRepository repository) {
        this.repository = repository;
    }


    @Override
    public Review getById(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Отзыв с id = " + id + " не найден")
        );
    }

    @Override
    public long getCountReviewForProduct(long productId) {
        return repository.countReviewForProduct(productId);
    }

    @Override
    public List<Review> getReviewForProduct(long productId, int page, int pageSize) {
        return repository.findReviewForProduct(productId, page, pageSize);
    }

    @Override
    public long getCountCommentsForReview(long reviewId) {
        return repository.countCommentsForReview(reviewId);
    }

    @Override
    public List<Review> getCommentsForReview(long reviewId, int page, int pageSize) {
        return repository.findCommentsForReview(reviewId, page, pageSize);
    }

    @Override
    public void saveReview(Review review) {
        repository.saveReview(review);
    }

    @Override
    public void saveComment(long reviewId, Review comment) {
        repository.saveComment(reviewId, comment);
    }
}
