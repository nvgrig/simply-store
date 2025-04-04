package ru.nvgrig.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.controller.payload.NewProductReviewPayload;
import ru.nvgrig.feedback.entity.ProductReview;
import ru.nvgrig.feedback.service.ProductReviewsService;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @GetMapping("by-product-id/{productId}")
    @Operation(
            security = @SecurityRequirement(name = "keycloak")
    )
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable("productId") int productId) {
        //return productReviewsService.findProductReviewsByProduct(productId);
        return reactiveMongoTemplate
                .find(Query.query(Criteria.where("productId").is(productId)), ProductReview.class);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return authenticationTokenMono.flatMap(token -> payloadMono
                .flatMap(payload -> productReviewsService.createProductReview(payload.productId(),
                        payload.rating(), payload.review(), token.getToken().getSubject())))
                .map(productReview -> ResponseEntity.created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(productReview.getId()))
                        .body(productReview));
    }
}
