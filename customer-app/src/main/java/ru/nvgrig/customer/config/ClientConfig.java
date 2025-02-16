package ru.nvgrig.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nvgrig.customer.client.WebClientProductsClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${store.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl) {
        return new WebClientProductsClient(WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build());
    }
}
