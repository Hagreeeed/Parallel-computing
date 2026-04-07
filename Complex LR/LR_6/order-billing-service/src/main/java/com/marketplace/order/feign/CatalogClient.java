package com.marketplace.order.feign;

import com.marketplace.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalog-service", url = "${CATALOG_SERVICE_URL:http://localhost:8082}", fallback = CatalogClientFallback.class)
public interface CatalogClient {
    @GetMapping("/api/internal/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/internal/products/{id}/decrease-stock")
    void decreaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PutMapping("/api/internal/products/{id}/increase-stock")
    void increaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
