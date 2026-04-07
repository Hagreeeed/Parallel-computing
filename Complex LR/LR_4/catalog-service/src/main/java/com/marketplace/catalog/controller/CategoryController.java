package com.marketplace.catalog.controller;
import com.marketplace.catalog.dto.request.CreateCategoryRequest;
import com.marketplace.catalog.model.Category;
import com.marketplace.catalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping public ResponseEntity<Page<Category>> getAll(Pageable pageable) { return ResponseEntity.ok(categoryService.findAll(pageable)); }
    @GetMapping("/{id}") public ResponseEntity<Category> getById(@PathVariable Long id) { return ResponseEntity.ok(categoryService.findById(id)); }
    @GetMapping("/{id}/subcategories") public ResponseEntity<List<Category>> getSub(@PathVariable Long id) { return ResponseEntity.ok(categoryService.findSubcategories(id)); }
    @PostMapping public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequest req) { return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req)); }
    @PutMapping("/{id}") public ResponseEntity<Category> update(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest req) { return ResponseEntity.ok(categoryService.update(id, req)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { categoryService.delete(id); return ResponseEntity.noContent().build(); }
}
