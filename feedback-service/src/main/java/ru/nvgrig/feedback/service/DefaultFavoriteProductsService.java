package ru.nvgrig.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.entity.FavoriteProduct;
import ru.nvgrig.feedback.repository.FavoriteProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavoriteProductsService implements FavoriteProductsService {

    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(int productId, String userId) {
        return favoriteProductRepository.save(new FavoriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId, String userId) {
        return favoriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavoriteProduct> findByProduct(int productId, String userId) {
        return favoriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts(String userId) {
        return favoriteProductRepository.findAllByUserId(userId);
    }
}
