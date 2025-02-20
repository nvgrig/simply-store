package ru.nvgrig.feedback.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.entity.ProductReview;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review);

    Flux<ProductReview> findProductReviewsByProduct(int productId);
}
