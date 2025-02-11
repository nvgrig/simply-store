package ru.nvgrig.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.nvgrig.manager.client.RestClientProductsRestClient;

@Configuration
public class TestingBeans {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;
    @MockBean
    OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository;
    @Bean
    @Primary
    public RestClientProductsRestClient testRestClientProductsRestClient(
            @Value("${store.services.catalogue.uri:http//localhost:54321}") String catalogueBaseUri
    ) {
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    };
}
