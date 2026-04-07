package com.marketplace.catalog.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
public class CreateProductRequest {
    @NotNull(message = "Seller ID is required") private Long sellerId;
    private List<Long> categoryIds;
    @NotBlank(message = "Product name is required") private String name;
    private String description;
    @NotNull(message = "Price is required") @Positive(message = "Price must be positive") private Double price;
    @NotNull(message = "Stock is required") private Integer stock;
    public Long getSellerId() { return sellerId; } public void setSellerId(Long s) { this.sellerId = s; }
    public List<Long> getCategoryIds() { return categoryIds; } public void setCategoryIds(List<Long> c) { this.categoryIds = c; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public Double getPrice() { return price; } public void setPrice(Double p) { this.price = p; }
    public Integer getStock() { return stock; } public void setStock(Integer s) { this.stock = s; }
}
