package com.marketplace.service;

import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Commission;
import com.marketplace.repository.CommissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public CommissionService(CommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }
    
    public Page<Commission> findAll(Pageable pageable) {
        return commissionRepository.findAll(pageable);
    }

    public Page<Commission> findBySellerId(Long sellerId, Pageable pageable) {
        return commissionRepository.findBySeller_Id(sellerId, pageable);
    }
    
    public Commission findById(Long id) {
        return commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", id));
    }

    @Transactional
    public Commission markAsPaid(Long id) {
        Commission commission = findById(id);

        if (commission.getPaid()) {
            throw new BusinessRuleException("Commission " + id + " is already paid");
        }

        commission.setPaid(true);
        return commissionRepository.save(commission);
    }
    
    @Transactional
    public Commission update(Long id, Boolean paid) {
        Commission commission = findById(id);
        if (paid != null) {
            commission.setPaid(paid);
        }
        return commissionRepository.save(commission);
    }
    
    @Transactional
    public void delete(Long id) {
        Commission commission = findById(id);
        commissionRepository.delete(commission);
    }
}
