package org.example.service;

import org.example.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    /* Методы для пользователя */

    Optional<Product> getProduct(Long id);

    List<Product> getPageProduct(Long page);

    long getProductCount();

    /* Методы для админа */

    void saveProduct(Product product);
}
