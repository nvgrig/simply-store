package ru.nvgrig.customer.client;

import reactor.core.publisher.Flux;
import ru.nvgrig.customer.entity.Product;

public interface ProductsClient {

    Flux<Product> findAllProducts(String filter);
}
