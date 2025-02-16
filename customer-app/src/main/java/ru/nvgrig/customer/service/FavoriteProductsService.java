package ru.nvgrig.customer.service;

import reactor.core.publisher.Mono;
import ru.nvgrig.customer.entity.FavoriteProduct;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavoriteProduct> findByProduct(int productId);
}
