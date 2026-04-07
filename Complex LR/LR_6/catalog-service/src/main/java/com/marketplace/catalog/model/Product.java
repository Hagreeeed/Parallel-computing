package com.marketplace.catalog.model;
import com.marketplace.catalog.model.enums.ProductStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "seller_id", nullable = false) private Long sellerId;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();
    @Column(nullable = false) private String name;
    private String description;
    @Column(nullable = false) private Double price;
    @Column(nullable = false) private Integer stock;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ProductStatus status;
    @Column(nullable = false) private LocalDateTime createdAt;

    public Product() {}
    public Product(Long sellerId, List<Category> categories, String name, String description, Double price, Integer stock) {
        this.sellerId = sellerId; this.categories = categories != null ? categories : new ArrayList<>();
        this.name = name; this.description = description; this.price = price; this.stock = stock;
        this.status = stock > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getSellerId() { return sellerId; } public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public List<Category> getCategories() { return categories; } public void setCategories(List<Category> c) { this.categories = c; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public Double getPrice() { return price; } public void setPrice(Double p) { this.price = p; }
    public Integer getStock() { return stock; } public void setStock(Integer s) { this.stock = s; if (this.stock == 0) this.status = ProductStatus.OUT_OF_STOCK; }
    public ProductStatus getStatus() { return status; } public void setStatus(ProductStatus s) { this.status = s; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}
