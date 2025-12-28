package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Equipment {
    private int equipmentId;
    private String equipmentCode;
    private int categoryId;
    private String categoryName;
    private String brand;
    private String model;
    private int purchaseYear;
    private BigDecimal dailyBasePrice;
    private BigDecimal securityDeposit;
    private EquipmentStatus status;
    private int branchId;
    private String branchName;
    private LocalDateTime createdAt;
    
    public enum EquipmentStatus {
        AVAILABLE, RESERVED, RENTED, UNDER_MAINTENANCE
    }
    
    // Constructor
    public Equipment() {}
    
    public Equipment(String equipmentCode, int categoryId, String brand, String model, 
                     int purchaseYear, BigDecimal dailyBasePrice, BigDecimal securityDeposit, int branchId) {
        this.equipmentCode = equipmentCode;
        this.categoryId = categoryId;
        this.brand = brand;
        this.model = model;
        this.purchaseYear = purchaseYear;
        this.dailyBasePrice = dailyBasePrice;
        this.securityDeposit = securityDeposit;
        this.status = EquipmentStatus.AVAILABLE;
        this.branchId = branchId;
    }
    
    // Getters and Setters
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public String getEquipmentCode() {
        return equipmentCode;
    }
    
    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }
    
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
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getPurchaseYear() {
        return purchaseYear;
    }
    
    public void setPurchaseYear(int purchaseYear) {
        this.purchaseYear = purchaseYear;
    }
    
    public BigDecimal getDailyBasePrice() {
        return dailyBasePrice;
    }
    
    public void setDailyBasePrice(BigDecimal dailyBasePrice) {
        this.dailyBasePrice = dailyBasePrice;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public EquipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }
    
    public int getBranchId() {
        return branchId;
    }
    
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return equipmentCode + " - " + brand + " " + model + " (" + categoryName + ")";
    }
}