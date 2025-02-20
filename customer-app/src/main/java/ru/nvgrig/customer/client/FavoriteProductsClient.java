package ru.nvgrig.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.entity.FavoriteProduct;

public interface FavoriteProductsClient {

    Mono<FavoriteProduct> findByProduct(int id);
        
    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
