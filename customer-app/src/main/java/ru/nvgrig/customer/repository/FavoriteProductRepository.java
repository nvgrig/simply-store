package ru.nvgrig.customer.repository;

import reactor.core.publisher.Mono;
import ru.nvgrig.customer.entity.FavoriteProduct;

public interface FavoriteProductRepository {

    Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(int productId);

    Mono<FavoriteProduct> findByProductId(int productId);
}
