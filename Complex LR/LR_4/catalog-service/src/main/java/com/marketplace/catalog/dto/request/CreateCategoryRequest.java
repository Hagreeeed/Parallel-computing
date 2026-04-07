package com.marketplace.catalog.dto.request;
import jakarta.validation.constraints.NotBlank;
public class CreateCategoryRequest {
    @NotBlank(message = "Name is required") private String name;
    private String description;
    private Long parentId;
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public Long getParentId() { return parentId; } public void setParentId(Long p) { this.parentId = p; }
}
