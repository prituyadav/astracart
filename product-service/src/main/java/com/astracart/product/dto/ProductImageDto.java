package com.astracart.product.dto;

import java.time.LocalDateTime;

public class ProductImageDto {

    private Long id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    public ProductImageDto() {}

    public ProductImageDto(Long id, String imageUrl, String altText, Integer displayOrder, LocalDateTime createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
