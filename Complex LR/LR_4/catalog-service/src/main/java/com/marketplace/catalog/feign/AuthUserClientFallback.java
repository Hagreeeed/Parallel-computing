package com.marketplace.catalog.feign;

import com.marketplace.dto.SellerDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthUserClientFallback implements AuthUserClient {
    @Override
    public SellerDTO getSellerById(Long id) {
        SellerDTO fallback = new SellerDTO();
        fallback.setId(id);
        fallback.setShopName("Service unavailable");
        fallback.setVerified(false);
        return fallback;
    }
}
