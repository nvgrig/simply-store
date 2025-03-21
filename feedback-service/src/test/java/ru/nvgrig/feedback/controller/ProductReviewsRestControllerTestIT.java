package ru.nvgrig.feedback.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import ru.nvgrig.feedback.entity.ProductReview;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductReviewsRestControllerTestIT {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("34f042c2-b624-469a-ac7f-0264b121eb64"), 1, 1, "Отзыв 1", "user-1"),
                new ProductReview(UUID.fromString("100f1f4a-a9a5-42af-85f8-91d916537ffd"), 1, 3, "Отзыв 2", "user-2"),
                new ProductReview(UUID.fromString("890e1cee-1a2d-4479-8793-ab00a1101136"), 1, 5, "Отзыв 3", "user-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        webTestClient.mutateWith(mockJwt())
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("===Request===");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, value) -> log.info("{}: {}", header, value));
                    log.info("=End Request=");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""                        
                        [
                            {"id": "34f042c2-b624-469a-ac7f-0264b121eb64", "productId": 1, "rating": 1, "review": "Отзыв 1", "userId": "user-1"},
                            {"id": "100f1f4a-a9a5-42af-85f8-91d916537ffd", "productId": 1, "rating": 3, "review": "Отзыв 2", "userId": "user-2"},
                            {"id": "890e1cee-1a2d-4479-8793-ab00a1101136", "productId": 1, "rating": 5, "review": "Отзыв 3", "userId": "user-3"}
                        ]
                        """);

    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "productId": 1,
                          "rating": 5,
                          "review": "отлично"
                        }""")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                          "productId": 1,
                          "rating": 5,
                          "review": "отлично",
                          "userId": "user-tester"
                        }""")
                .jsonPath("$.id").exists()
                .consumeWith(document("feedback/product_reviews/create_product_review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").description("Идентификатор товара").type("int"),
                                fieldWithPath("rating").description("Оценка товара").type("int"),
                                fieldWithPath("review").description("Отзыв товара").type("string")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Идентификатор отзыва").type("uuid"),
                                fieldWithPath("productId").description("Идентификатор товара").type("int"),
                                fieldWithPath("rating").description("Оценка товара").type("int"),
                                fieldWithPath("review").description("Отзыв товара").type("string"),
                                fieldWithPath("userId").description("Идентификатор пользователя").type("string")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Ссылка на созданный отзыв о товаре")
                        )));
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "productId": null,
                          "rating": -1,
                          "review": "отлично"
                        }""")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "errors": [
                            "товар не указан",
                            "оценка меньше 1"
                          ]
                        }""");
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        webTestClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
