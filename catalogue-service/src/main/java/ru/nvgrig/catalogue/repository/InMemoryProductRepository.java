package ru.nvgrig.catalogue.repository;

import org.springframework.stereotype.Repository;
import ru.nvgrig.catalogue.entity.Product;

import java.util.*;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public Product save(Product product) {
        product.setId(products.stream()
                .max(Comparator.comparing(Product::getId))
                .map(Product::getId)
                .orElse(0) + 1);
        products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer productId) {
        return products.stream()
                .filter(product -> Objects.equals(product.getId(), productId))
                .findFirst();
    }

    @Override
    public void deleteById(Integer id) {
        products.removeIf(product -> Objects.equals(product.getId(), id));
    }

}
