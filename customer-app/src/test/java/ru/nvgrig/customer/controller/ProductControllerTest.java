package ru.nvgrig.customer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.nvgrig.customer.client.FavoriteProductsClient;
import ru.nvgrig.customer.client.ProductReviewsClient;
import ru.nvgrig.customer.client.ProductsClient;
import ru.nvgrig.customer.entity.Product;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;
    @Mock
    FavoriteProductsClient favoriteProductsClient;
    @Mock
    ProductReviewsClient productReviewsClient;
    @InjectMocks
    ProductController productController;

    @Test
    @DisplayName("Исключение NoSuchElementException транслируется в страницу error/404")
    void handleNoSuchElementException_Returns404Page() {
        NoSuchElementException exception = new NoSuchElementException("Товар не найден");
        ConcurrentModel model = new ConcurrentModel();

        String actual = productController.handleNoSuchElementException(exception, model);

        assertEquals("errors/404", actual);
        assertEquals("Товар не найден", model.getAttribute("error"));
    }

    @Test
    void loadProduct_ProductExists_ReturnsNotEmptyMono() {
        Product product = new Product(1, "Товар", "Описание");
        when(productsClient.findProduct(1)).thenReturn(Mono.just(product));

        StepVerifier.create(productController.loadProduct(1))
                .expectNext(new Product(1, "Товар", "Описание"))
                .expectComplete()
                .verify();

        verify(productsClient).findProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favoriteProductsClient, productReviewsClient);
    }

    @Test
    void loadProduct_ProductDoesNotExists_ReturnsNoSuchElementException() {
        when(productsClient.findProduct(1)).thenReturn(Mono.empty());

        StepVerifier.create(productController.loadProduct(1))
                .expectErrorMatches(exception ->
                        exception instanceof NoSuchElementException e &&
                        e.getMessage().equals("customer.products.error.not_found"))
                .verify();

        verify(productsClient).findProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favoriteProductsClient, productReviewsClient);
    }

}