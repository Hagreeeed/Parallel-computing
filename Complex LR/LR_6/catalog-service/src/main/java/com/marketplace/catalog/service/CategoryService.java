package com.marketplace.catalog.service;
import com.marketplace.catalog.dto.request.CreateCategoryRequest;
import com.marketplace.catalog.exception.ResourceNotFoundException;
import com.marketplace.catalog.model.Category;
import com.marketplace.catalog.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository; }
    public Page<Category> findAll(Pageable pageable) { return categoryRepository.findAll(pageable); }
    public Category findById(Long id) { return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", id)); }
    public List<Category> findSubcategories(Long parentId) { findById(parentId); return categoryRepository.findByParent_Id(parentId); }
    @Transactional
    public Category create(CreateCategoryRequest request) {
        Category parent = request.getParentId() != null ? findById(request.getParentId()) : null;
        return categoryRepository.save(new Category(request.getName(), request.getDescription(), parent));
    }
    @Transactional
    public Category update(Long id, CreateCategoryRequest request) {
        Category c = findById(id); Category parent = request.getParentId() != null ? findById(request.getParentId()) : null;
        c.setName(request.getName()); c.setDescription(request.getDescription()); c.setParent(parent);
        return categoryRepository.save(c);
    }
    @Transactional
    public void delete(Long id) { categoryRepository.delete(findById(id)); }
}
