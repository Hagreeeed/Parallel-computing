package com.marketplace.service;

import com.marketplace.dto.request.CreateProductRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Category;
import com.marketplace.model.Product;
import com.marketplace.model.Seller;
import com.marketplace.model.enums.ProductStatus;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.SellerRepository;
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
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, SellerRepository sellerRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Product> findAll(Long categoryId, Long sellerId, Pageable pageable) {
        Page<Product> productsPage;
        if (categoryId != null) {
            productsPage = productRepository.findByCategories_Id(categoryId, pageable);
        } else if (sellerId != null) {
            productsPage = productRepository.findBySeller_Id(sellerId, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }
        
        List<Product> activeProducts = productsPage.getContent().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
                
        return new PageImpl<>(activeProducts, pageable, productsPage.getTotalElements());
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional
    public Product create(CreateProductRequest request) {
        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller", request.getSellerId()));

        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new BusinessRuleException("Some categories not found");
            }
        }

        if (request.getStock() < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }

        Product product = new Product(
                seller,
                categories,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, CreateProductRequest request) {
        Product product = findById(id);
        
        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new BusinessRuleException("Some categories not found");
            }
        }
        
        product.setCategories(categories);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return productRepository.save(product);
    }

    @Transactional
    public Product deactivate(Long id) {
        Product product = findById(id);
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new BusinessRuleException("Product " + id + " is already inactive");
        }
        product.setStatus(ProductStatus.INACTIVE);
        return productRepository.save(product);
    }
    
    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
