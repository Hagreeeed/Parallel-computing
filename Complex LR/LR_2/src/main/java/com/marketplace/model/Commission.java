package com.marketplace.model;

import java.time.LocalDateTime;

public class Commission {

    private Long id;
    private Long orderId;
    private Long sellerId;
    private Double orderAmount;
    private Double rate;
    private Double amount;
    private Boolean paid;
    private LocalDateTime createdAt;

    public Commission() {
    }

    public Commission(Long orderId, Long sellerId, Double orderAmount, Double rate) {
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.orderAmount = orderAmount;
        this.rate = rate;
        this.amount = orderAmount * rate;
        this.paid = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
