package com.marketplace.order.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {
    private Long productId;
    private Long sellerId;
    private Integer quantity;
    private Double priceAtTime;
    private Double subtotal;

    public OrderItem() {}
    public OrderItem(Long productId, Long sellerId, Integer quantity, Double priceAtTime) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.subtotal = quantity * priceAtTime;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(Double priceAtTime) { this.priceAtTime = priceAtTime; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
