package com.marketplace.repository;

import com.marketplace.model.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Page<Commission> findBySeller_Id(Long sellerId, Pageable pageable);
    List<Commission> findBySeller_Id(Long sellerId);
}
