package com.marketplace.order.feign;

import com.marketplace.dto.ProductDTO;
import com.marketplace.order.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class CatalogClientFallback implements CatalogClient {
    @Override
    public ProductDTO getProductById(Long id) {
        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setName("Service unavailable");
        dto.setPrice(1.0); // Degraded default price
        return dto;
    }

    @Override
    public void decreaseStock(Long id, Integer quantity) {
        throw new BusinessRuleException("Cannot decrease stock, Catalog Service is unavailable");
    }

    @Override
    public void increaseStock(Long id, Integer quantity) {
        // Just ignore if we can't increase, or log it
    }
}
