package ru.nvgrig.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductControllerTestIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addProductToFavorites_RequestIsValid_ReturnsRedirectionToProductPage() {
        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                                {
                                    "id": 1,
                                    "title": "Товар 1",
                                    "details": "Описание товара 1"
                                }""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        WireMock.stubFor(WireMock.post("/feedback-api/favorite-products")
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "productId": 1
                        }
                        """))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "34f042c2-b624-469a-ac7f-0264b121eb64",
                                    "productId": 1
                                }""")));

        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favorites")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        WireMock.verify(getRequestedFor(urlPathMatching("/catalogue-api/products/1")));
        WireMock.verify(postRequestedFor(urlPathMatching("/feedback-api/favorite-products"))
                .withRequestBody(equalToJson("""
                        {
                          "productId": 1
                        }""")));
    }

    @Test
    void addProductToFavorites_ProductDoesNotExist_ReturnsNotFoundPage() {
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favorites")
                .exchange()
                .expectStatus().isNotFound();

        WireMock.verify(getRequestedFor(urlPathMatching("/catalogue-api/products/1")));
    }
}