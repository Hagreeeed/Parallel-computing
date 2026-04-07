package com.marketplace.order.feign;

import com.marketplace.dto.CustomerDTO;
import com.marketplace.dto.SellerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "auth-user-service", url = "${AUTH_SERVICE_URL:http://localhost:8081}", fallback = AuthUserClientFallback.class)
public interface AuthUserClient {
    @GetMapping("/api/internal/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") Long id);

    @GetMapping("/api/internal/sellers/{id}")
    SellerDTO getSellerById(@PathVariable("id") Long id);

    @PutMapping("/api/internal/sellers/{id}/increment-sales")
    void incrementSales(@PathVariable("id") Long id);
}
