package com.marketplace.catalog.feign;

import com.marketplace.dto.SellerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-user-service", url = "${AUTH_SERVICE_URL:http://localhost:8081}", fallback = AuthUserClientFallback.class)
public interface AuthUserClient {
    @GetMapping("/api/internal/sellers/{id}")
    SellerDTO getSellerById(@PathVariable("id") Long id);
}
