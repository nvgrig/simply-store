package ru.nvgrig.manager.repository;

import ru.nvgrig.manager.entity.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> findAll();

    Product save(Product product);

}
