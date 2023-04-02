package org.example.repository.category;

import org.example.model.category.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    long count();
    Optional<Category> findById(UUID id);
    void save(Category category);
}
