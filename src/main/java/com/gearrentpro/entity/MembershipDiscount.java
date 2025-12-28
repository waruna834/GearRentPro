package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipDiscount {
    private int discountId;
    private String membershipLevel;
    private BigDecimal discountPercentage;
    private LocalDateTime updatedAt;
    
    // Constructor
    public MembershipDiscount() {}
    
    public MembershipDiscount(String membershipLevel, BigDecimal discountPercentage) {
        this.membershipLevel = membershipLevel;
        this.discountPercentage = discountPercentage;
    }
    
    // Getters and Setters
    public int getDiscountId() {
        return discountId;
    }
    
    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }
    
    public String getMembershipLevel() {
        return membershipLevel;
    }
    
    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return membershipLevel + " - " + discountPercentage + "%";
    }
}