package com.marketplace.catalog.repository;
import com.marketplace.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    List<Product> findBySellerId(Long sellerId);
    Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);
}
