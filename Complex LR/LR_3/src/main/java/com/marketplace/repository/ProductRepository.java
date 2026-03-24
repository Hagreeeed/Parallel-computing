package com.marketplace.repository;

import com.marketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findBySeller_Id(Long sellerId, Pageable pageable);
    List<Product> findBySeller_Id(Long sellerId);
    Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);
    List<Product> findByCategories_Id(Long categoryId);
}
