package com.marketplace.auth.controller;

import com.marketplace.auth.model.Seller;
import com.marketplace.auth.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final SellerService sellerService;
    public AdminController(SellerService sellerService) { this.sellerService = sellerService; }

    @PutMapping("/sellers/{id}/verify")
    public ResponseEntity<Seller> verifySeller(@PathVariable Long id) { return ResponseEntity.ok(sellerService.verifySeller(id)); }
}
