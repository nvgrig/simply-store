package ru.nvgrig.customer.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nvgrig.customer.client.WebClientFavoriteProductsClient;
import ru.nvgrig.customer.client.WebClientProductReviewsClient;
import ru.nvgrig.customer.client.WebClientProductsClient;

@Configuration
public class TestBeans {

    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Bean
    @Primary
    public WebClientProductsClient mockWebClientProductsClient() {
        return new WebClientProductsClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientProductReviewsClient mockWebClientProductReviewsClient() {
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientFavoriteProductsClient mockWebClientFavoriteProductsClient() {
        return new WebClientFavoriteProductsClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }
}
