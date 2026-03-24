package com.marketplace.service;

import com.marketplace.dto.request.CreateCategoryRequest;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Category;
import com.marketplace.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public List<Category> findSubcategories(Long parentId) {
        findById(parentId); // verify parent exists
        return categoryRepository.findByParent_Id(parentId);
    }

    @Transactional
    public Category create(CreateCategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = findById(request.getParentId()); // verify parent exists
        }
        Category category = new Category(
                request.getName(),
                request.getDescription(),
                parent);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CreateCategoryRequest request) {
        Category category = findById(id);
        Category parent = null;
        if (request.getParentId() != null) {
            parent = findById(request.getParentId()); 
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
