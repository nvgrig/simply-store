package ru.nvgrig.catalogue.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.nvgrig.catalogue.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/sql/products.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTestIT {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findAllBYTitleLikeIgnoringCase_ReturnsFilteredProductList() {
        var filter = "%молоко%";

        var products = productRepository.findAllByTitleLikeIgnoreCase(filter);

        assertEquals(List.of(new Product(2, "Молоко", "Так это же просто молоко")), products);
    }
}