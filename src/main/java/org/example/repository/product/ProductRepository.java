package org.example.repository.product;


import org.example.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Long count();
    List<Product> findAll(long page, int pageSize);
    Optional<Product> findById(long id);
    void save(Product product);
}
