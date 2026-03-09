package com.marketplace.service;

import com.marketplace.dto.request.CreateProductRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Product;
import com.marketplace.model.enums.ProductStatus;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.SellerRepository;
import org.springframework.stereotype.Service;

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

    public List<Product> findAll(Long categoryId, Long sellerId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (sellerId != null) {
            products = productRepository.findBySellerId(sellerId);
        } else {
            products = productRepository.findAll();
        }
        return products.stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    public Product create(CreateProductRequest request) {
        sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller", request.getSellerId()));

        if (request.getCategoryIds() != null) {
            for (Long catId : request.getCategoryIds()) {
                categoryRepository.findById(catId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", catId));
            }
        }

        if (request.getStock() < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }

        Product product = new Product(
                request.getSellerId(),
                request.getCategoryIds(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock());
        return productRepository.save(product);
    }

    public Product deactivate(Long id) {
        Product product = findById(id);
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new BusinessRuleException("Product " + id + " is already inactive");
        }
        product.setStatus(ProductStatus.INACTIVE);
        return productRepository.save(product);
    }
}
