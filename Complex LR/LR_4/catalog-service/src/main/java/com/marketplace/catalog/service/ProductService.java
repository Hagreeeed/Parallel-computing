package com.marketplace.catalog.service;
import com.marketplace.catalog.dto.request.CreateProductRequest;
import com.marketplace.catalog.exception.BusinessRuleException;
import com.marketplace.catalog.exception.ResourceNotFoundException;
import com.marketplace.catalog.model.Category;
import com.marketplace.catalog.model.Product;
import com.marketplace.catalog.model.enums.ProductStatus;
import com.marketplace.catalog.repository.CategoryRepository;
import com.marketplace.catalog.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository; this.categoryRepository = categoryRepository;
    }
    public Page<Product> findAll(Long categoryId, Long sellerId, Pageable pageable) {
        Page<Product> page;
        if (categoryId != null) page = productRepository.findByCategories_Id(categoryId, pageable);
        else if (sellerId != null) page = productRepository.findBySellerId(sellerId, pageable);
        else page = productRepository.findAll(pageable);
        List<Product> active = page.getContent().stream().filter(p -> p.getStatus() == ProductStatus.ACTIVE).collect(Collectors.toList());
        return new PageImpl<>(active, pageable, page.getTotalElements());
    }
    public Product findById(Long id) { return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id)); }
    @Transactional
    public Product create(CreateProductRequest request) {
        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) throw new BusinessRuleException("Some categories not found");
        }
        if (request.getStock() < 0) throw new BusinessRuleException("Stock cannot be negative");
        return productRepository.save(new Product(request.getSellerId(), categories, request.getName(), request.getDescription(), request.getPrice(), request.getStock()));
    }
    @Transactional
    public Product update(Long id, CreateProductRequest request) {
        Product p = findById(id);
        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) throw new BusinessRuleException("Some categories not found");
        }
        p.setCategories(categories); p.setName(request.getName()); p.setDescription(request.getDescription());
        p.setPrice(request.getPrice()); p.setStock(request.getStock());
        return productRepository.save(p);
    }
    @Transactional
    public Product deactivate(Long id) {
        Product p = findById(id);
        if (p.getStatus() == ProductStatus.INACTIVE) throw new BusinessRuleException("Product " + id + " is already inactive");
        p.setStatus(ProductStatus.INACTIVE);
        return productRepository.save(p);
    }
    @Transactional
    public void delete(Long id) { productRepository.delete(findById(id)); }
}
