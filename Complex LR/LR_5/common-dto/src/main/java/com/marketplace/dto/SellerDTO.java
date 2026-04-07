package com.marketplace.dto;

public class SellerDTO {
    private Long id;
    private Long userId;
    private String shopName;
    private String description;
    private Double rating;
    private Integer totalSales;
    private Double commissionRate;
    private Boolean verified;

    public SellerDTO() {}

    public SellerDTO(Long id, Long userId, String shopName, String description, Double rating, Integer totalSales, Double commissionRate, Boolean verified) {
        this.id = id;
        this.userId = userId;
        this.shopName = shopName;
        this.description = description;
        this.rating = rating;
        this.totalSales = totalSales;
        this.commissionRate = commissionRate;
        this.verified = verified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
    public Double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(Double commissionRate) { this.commissionRate = commissionRate; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}
