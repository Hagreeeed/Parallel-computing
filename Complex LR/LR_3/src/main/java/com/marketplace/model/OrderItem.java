package com.marketplace.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {

    private Long productId;
    private Long sellerId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;

    public OrderItem() {
    }

    public OrderItem(Long productId, Long sellerId, String productName,
            Double unitPrice, Integer quantity) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice * quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice * this.quantity;
        }
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
