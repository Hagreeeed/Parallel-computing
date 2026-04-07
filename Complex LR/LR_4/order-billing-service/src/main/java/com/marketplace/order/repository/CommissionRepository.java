package com.marketplace.order.repository;

import com.marketplace.order.model.Commission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Page<Commission> findBySellerId(Long sellerId, Pageable pageable);
    List<Commission> findBySellerId(Long sellerId);
}
