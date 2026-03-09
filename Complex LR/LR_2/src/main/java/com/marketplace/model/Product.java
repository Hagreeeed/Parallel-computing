package com.marketplace.model;

import com.marketplace.model.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Product {

    private Long id;
    private Long sellerId;
    private List<Long> categoryIds;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private ProductStatus status;
    private LocalDateTime createdAt;

    public Product() {
        this.categoryIds = new ArrayList<>();
    }

    public Product(Long sellerId, List<Long> categoryIds, String name, String description,
            Double price, Integer stock) {
        this.sellerId = sellerId;
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = stock > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
        if (this.stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
