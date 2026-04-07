package com.marketplace.controller;

import com.marketplace.model.Seller;
import com.marketplace.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final SellerService sellerService;

    public AdminController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PutMapping("/sellers/{id}/verify")
    public ResponseEntity<Seller> verifySeller(@PathVariable Long id) {
        Seller verifiedSeller = sellerService.verifySeller(id);
        return ResponseEntity.ok(verifiedSeller);
    }
}
