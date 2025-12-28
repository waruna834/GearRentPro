package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReturnDetails {
    private int returnId;
    private int rentalId;
    private String damageDescription;
    private BigDecimal damageCharge;
    private BigDecimal lateFee;
    private BigDecimal totalCharges;
    private BigDecimal refundAmount;
    private BigDecimal additionalPaymentRequired;
    private LocalDateTime createdAt;
    
    // Constructor
    public ReturnDetails() {}
    
    public ReturnDetails(int rentalId) {
        this.rentalId = rentalId;
        this.damageCharge = BigDecimal.ZERO;
        this.lateFee = BigDecimal.ZERO;
        this.totalCharges = BigDecimal.ZERO;
        this.refundAmount = BigDecimal.ZERO;
        this.additionalPaymentRequired = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public int getReturnId() {
        return returnId;
    }
    
    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }
    
    public int getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }
    
    public String getDamageDescription() {
        return damageDescription;
    }
    
    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }
    
    public BigDecimal getDamageCharge() {
        return damageCharge;
    }
    
    public void setDamageCharge(BigDecimal damageCharge) {
        this.damageCharge = damageCharge;
    }
    
    public BigDecimal getLateFee() {
        return lateFee;
    }
    
    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }
    
    public BigDecimal getTotalCharges() {
        return totalCharges;
    }
    
    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public BigDecimal getAdditionalPaymentRequired() {
        return additionalPaymentRequired;
    }
    
    public void setAdditionalPaymentRequired(BigDecimal additionalPaymentRequired) {
        this.additionalPaymentRequired = additionalPaymentRequired;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "ReturnDetails{" +
                "rentalId=" + rentalId +
                ", damageCharge=" + damageCharge +
                ", lateFee=" + lateFee +
                ", totalCharges=" + totalCharges +
                '}';
    }
}