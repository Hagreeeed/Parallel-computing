package com.marketplace.auth.controller;

import com.marketplace.auth.model.Customer;
import com.marketplace.auth.model.Seller;
import com.marketplace.auth.service.CustomerService;
import com.marketplace.auth.service.SellerService;
import com.marketplace.dto.CustomerDTO;
import com.marketplace.dto.SellerDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class InternalController {
    private final CustomerService customerService;
    private final SellerService sellerService;

    public InternalController(CustomerService customerService, SellerService sellerService) {
        this.customerService = customerService; this.sellerService = sellerService;
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        Customer c = customerService.findById(id);
        return ResponseEntity.ok(new CustomerDTO(c.getId(), c.getUser().getId(), c.getFirstName(), c.getLastName(), c.getPhone(), c.getShippingAddress()));
    }

    @GetMapping("/sellers/{id}")
    public ResponseEntity<SellerDTO> getSeller(@PathVariable Long id) {
        Seller s = sellerService.findById(id);
        return ResponseEntity.ok(new SellerDTO(s.getId(), s.getUser().getId(), s.getShopName(), s.getDescription(), s.getRating(), s.getTotalSales(), s.getCommissionRate(), s.getVerified()));
    }

    @PutMapping("/sellers/{id}/increment-sales")
    public ResponseEntity<Void> incrementSales(@PathVariable Long id) {
        Seller s = sellerService.findById(id);
        s.setTotalSales(s.getTotalSales() + 1);
        return ResponseEntity.ok().build();
    }
}
