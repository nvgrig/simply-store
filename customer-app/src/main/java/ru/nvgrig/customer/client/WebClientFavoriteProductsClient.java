package ru.nvgrig.customer.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.client.exception.ClientBadRequestException;
import ru.nvgrig.customer.client.payload.NewFavoriteProductPayload;
import ru.nvgrig.customer.entity.FavoriteProduct;

import java.util.List;

@RequiredArgsConstructor
public class WebClientFavoriteProductsClient implements FavoriteProductsClient {

    private final WebClient webClient;

    @Override
    public Mono<FavoriteProduct> findByProduct(int id) {
        return webClient
                .get()
                .uri("/feedback-api/favorite-products/by-product-id/{productId}", id)
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(int productId) {
        return webClient
                .post()
                .uri("/feedback-api/favorite-products")
                .bodyValue(new NewFavoriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new ClientBadRequestException(exception,
                                (List<String>) exception.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors")));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId) {
        return webClient
                .delete()
                .uri("/feedback-api/favorite-products/by-product-id/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return webClient
                .get()
                .uri("/feedback-api/favorite-products")
                .retrieve()
                .bodyToFlux(FavoriteProduct.class);
    }
}
