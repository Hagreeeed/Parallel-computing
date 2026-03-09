package com.marketplace.service;

import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Commission;
import com.marketplace.repository.CommissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public CommissionService(CommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

    public List<Commission> findBySellerId(Long sellerId) {
        return commissionRepository.findBySellerId(sellerId);
    }

    public Commission markAsPaid(Long id) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", id));

        if (commission.getPaid()) {
            throw new BusinessRuleException("Commission " + id + " is already paid");
        }

        commission.setPaid(true);
        return commissionRepository.save(commission);
    }
}
