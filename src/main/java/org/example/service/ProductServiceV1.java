package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.product.Product;
import org.example.repository.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ProductServiceV1 implements ProductService {

    private final static Integer PAGE_SIZE = 25;

    private final ProductRepository repository;

    public ProductServiceV1(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        log.info("Поиск продукта с ID {}", id);
        return repository.findById(id);
    }

    @Override
    public List<Product> getPageProduct(Long page) {
        log.info("Возвращаем страницу {} с продуктами", page);
        return repository.findAll(page, PAGE_SIZE);
    }

    @Override
    public long getProductCount() {
        return repository.count();
    }

    @Override
    public void saveProduct(Product product) {
        log.info("Сохраняем продукт {}", product);
        repository.save(product);
    }
}
