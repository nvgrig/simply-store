package ru.nvgrig.manager.repository;

import org.springframework.stereotype.Repository;
import ru.nvgrig.manager.entity.Product;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

    public InMemoryProductRepository() {
        IntStream.range(1, 4)
                .forEach(i -> products.add(new Product(i, "Товар №%d".formatted(i), "Описание товара №%d".formatted(i))));
    }

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

}
