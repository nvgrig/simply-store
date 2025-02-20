package ru.nvgrig.feedback.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.entity.FavoriteProduct;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemoryFavoriteProductRepository implements FavoriteProductRepository {

    private final List<FavoriteProduct> favoriteProducts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct) {
        favoriteProducts.add(favoriteProduct);
        return Mono.just(favoriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        favoriteProducts.removeIf(favoriteProduct -> favoriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavoriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(favoriteProducts)
                .filter(favoriteProduct -> favoriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return Flux.fromIterable(favoriteProducts);
    }
}
