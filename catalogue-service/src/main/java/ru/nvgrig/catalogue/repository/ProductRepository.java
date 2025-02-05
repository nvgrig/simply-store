package ru.nvgrig.catalogue.repository;

import org.springframework.data.repository.CrudRepository;
import ru.nvgrig.catalogue.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    Iterable<Product> findAllByTitleLikeIgnoreCase(String filter);
    
}
