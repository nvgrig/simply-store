package ru.nvgrig.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.entity.ProductReview;

public interface ProductReviewsClient {

    Flux<ProductReview> findProductReviewsByProduct(int productId);

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);
}
