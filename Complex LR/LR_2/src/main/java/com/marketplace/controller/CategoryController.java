package com.marketplace.controller;

import com.marketplace.dto.request.CreateCategoryRequest;
import com.marketplace.model.Category;
import com.marketplace.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findSubcategories(id));
    }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
}
