package com.marketplace.service;

import com.marketplace.dto.request.CreateCategoryRequest;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Category;
import com.marketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public List<Category> findSubcategories(Long parentId) {
        findById(parentId); // verify parent exists
        return categoryRepository.findByParentId(parentId);
    }

    public Category create(CreateCategoryRequest request) {
        if (request.getParentId() != null) {
            findById(request.getParentId()); // verify parent exists
        }
        Category category = new Category(
                request.getName(),
                request.getDescription(),
                request.getParentId());
        return categoryRepository.save(category);
    }
}
