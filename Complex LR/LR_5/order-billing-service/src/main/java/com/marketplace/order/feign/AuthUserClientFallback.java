package com.marketplace.order.feign;

import com.marketplace.dto.CustomerDTO;
import com.marketplace.dto.SellerDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthUserClientFallback implements AuthUserClient {
    @Override
    public CustomerDTO getCustomerById(Long id) {
        CustomerDTO c = new CustomerDTO();
        c.setId(id);
        c.setFirstName("Service unavailable");
        return c;
    }

    @Override
    public SellerDTO getSellerById(Long id) {
        SellerDTO s = new SellerDTO();
        s.setId(id);
        s.setShopName("Service unavailable");
        s.setCommissionRate(0.05); // Degraded commission rate
        return s;
    }

    @Override
    public void incrementSales(Long id) {}
}
