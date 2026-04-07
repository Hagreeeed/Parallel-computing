package com.marketplace.controller;

import com.marketplace.model.Commission;
import com.marketplace.service.CommissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Commission>> getBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(commissionService.findBySellerId(sellerId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Commission> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(commissionService.markAsPaid(id));
    }
}
