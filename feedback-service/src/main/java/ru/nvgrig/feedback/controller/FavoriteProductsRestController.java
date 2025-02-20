package ru.nvgrig.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.controller.payload.NewFavoriteProductPayload;
import ru.nvgrig.feedback.entity.FavoriteProduct;
import ru.nvgrig.feedback.service.FavoriteProductsService;

@RestController
@RequestMapping("feedback-api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsRestController {

    private final FavoriteProductsService favoriteProductsService;

    @GetMapping
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return favoriteProductsService.findFavoriteProducts();
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> findFavoriteProductByProductId(@PathVariable("productId") int productId) {
        return favoriteProductsService.findByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<FavoriteProduct>> createFavoriteProduct(
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {

        return payloadMono
                .flatMap(payload -> favoriteProductsService.addProductToFavorites(payload.productId()))
                .map(favoriteProduct -> ResponseEntity.created(uriComponentsBuilder.replacePath("/feedback-api/favorite-products/{id}")
                                .build(favoriteProduct.getId()))
                        .body(favoriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(@PathVariable("productId") int productId) {
        return favoriteProductsService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
