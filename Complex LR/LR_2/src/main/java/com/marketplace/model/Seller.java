package com.marketplace.model;

public class Seller {

    private Long id;
    private Long userId;
    private String shopName;
    private String description;
    private Double rating;
    private Integer totalSales;
    private Double commissionRate;
    private Boolean verified;

    public Seller() {
    }

    public Seller(Long userId, String shopName, String description, Double commissionRate) {
        this.userId = userId;
        this.shopName = shopName;
        this.description = description;
        this.rating = 0.0;
        this.totalSales = 0;
        this.commissionRate = commissionRate;
        this.verified = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public Double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
