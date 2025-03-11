package ru.nvgrig.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nvgrig.customer.client.WebClientFavoriteProductsClient;
import ru.nvgrig.customer.client.WebClientProductReviewsClient;
import ru.nvgrig.customer.client.WebClientProductsClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder storeServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${store.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder storeServicesWebClientBuilder) {
        return new WebClientProductsClient(storeServicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${store.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder storeServicesWebClientBuilder) {
        return new WebClientProductReviewsClient(storeServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavoriteProductsClient webClientFavoriteProductsClient(
            @Value("${store.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder storeServicesWebClientBuilder) {
        return new WebClientFavoriteProductsClient(storeServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
