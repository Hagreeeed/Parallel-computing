package com.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BecomeSellerRequest {

    @NotBlank(message = "Shop name is required")
    private String shopName;

    private String description;
    private Double commissionRate;

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(Double commissionRate) { this.commissionRate = commissionRate; }
}
