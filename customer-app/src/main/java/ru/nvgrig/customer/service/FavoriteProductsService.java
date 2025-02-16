package ru.nvgrig.customer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.entity.FavoriteProduct;
import ru.nvgrig.customer.entity.Product;

import java.util.List;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavoriteProduct> findByProduct(int productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
