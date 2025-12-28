package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private BigDecimal basePriceFactor;
    private BigDecimal weekendMultiplier;
    private BigDecimal defaultLateFee;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    
    public enum CategoryStatus {
        ACTIVE, INACTIVE
    }
    
    // Constructor
    public Category() {}
    
    public Category(String categoryName, String description, BigDecimal basePriceFactor, 
                    BigDecimal weekendMultiplier, BigDecimal defaultLateFee) {
        this.categoryName = categoryName;
        this.description = description;
        this.basePriceFactor = basePriceFactor;
        this.weekendMultiplier = weekendMultiplier;
        this.defaultLateFee = defaultLateFee;
        this.status = CategoryStatus.ACTIVE;
    }
    
    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getBasePriceFactor() {
        return basePriceFactor;
    }
    
    public void setBasePriceFactor(BigDecimal basePriceFactor) {
        this.basePriceFactor = basePriceFactor;
    }
    
    public BigDecimal getWeekendMultiplier() {
        return weekendMultiplier;
    }
    
    public void setWeekendMultiplier(BigDecimal weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }
    
    public BigDecimal getDefaultLateFee() {
        return defaultLateFee;
    }
    
    public void setDefaultLateFee(BigDecimal defaultLateFee) {
        this.defaultLateFee = defaultLateFee;
    }
    
    public CategoryStatus getStatus() {
        return status;
    }
    
    public void setStatus(CategoryStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return categoryName;
    }
}