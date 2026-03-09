package com.marketplace.model;

import com.marketplace.model.enums.PaymentMethod;
import com.marketplace.model.enums.PaymentStatus;

import java.time.LocalDateTime;

public class Payment {

    private Long id;
    private Long orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private Double amount;
    private String transactionId;
    private LocalDateTime paidAt;

    public Payment() {
    }

    public Payment(Long orderId, PaymentMethod method, Double amount) {
        this.orderId = orderId;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.amount = amount;
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

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
