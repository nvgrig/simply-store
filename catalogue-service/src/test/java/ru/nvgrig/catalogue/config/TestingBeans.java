package ru.nvgrig.catalogue.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class TestingBeans {

    @MockBean
    JwtDecoder jwtDecoder;
}
