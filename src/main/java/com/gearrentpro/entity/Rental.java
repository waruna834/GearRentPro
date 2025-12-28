package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rental {
    private int rentalId;
    private String rentalCode;
    private int equipmentId;
    private String equipmentDetails;
    private int customerId;
    private String customerName;
    private int branchId;
    private String branchName;
    private Integer reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private BigDecimal dailyRate;
    private BigDecimal rentalAmount;
    private BigDecimal securityDeposit;
    private BigDecimal membershipDiscount;
    private BigDecimal longRentalDiscount;
    private BigDecimal finalPayableAmount;
    private PaymentStatus paymentStatus;
    private RentalStatus rentalStatus;
    private LocalDateTime createdAt;
    
    public enum PaymentStatus {
        PAID, PARTIALLY_PAID, UNPAID
    }
    
    public enum RentalStatus {
        ACTIVE, RETURNED, OVERDUE, CANCELLED
    }
    
    // Constructor
    public Rental() {}
    
    public Rental(String rentalCode, int equipmentId, int customerId, int branchId,
                  LocalDate startDate, LocalDate endDate, BigDecimal dailyRate,
                  BigDecimal rentalAmount, BigDecimal securityDeposit, BigDecimal finalPayableAmount) {
        this.rentalCode = rentalCode;
        this.equipmentId = equipmentId;
        this.customerId = customerId;
        this.branchId = branchId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.rentalAmount = rentalAmount;
        this.securityDeposit = securityDeposit;
        this.finalPayableAmount = finalPayableAmount;
        this.membershipDiscount = BigDecimal.ZERO;
        this.longRentalDiscount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.rentalStatus = RentalStatus.ACTIVE;
    }
    
    // Getters and Setters
    public int getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }
    
    public String getRentalCode() {
        return rentalCode;
    }
    
    public void setRentalCode(String rentalCode) {
        this.rentalCode = rentalCode;
    }
    
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public String getEquipmentDetails() {
        return equipmentDetails;
    }
    
    public void setEquipmentDetails(String equipmentDetails) {
        this.equipmentDetails = equipmentDetails;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    public Integer getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public BigDecimal getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public BigDecimal getRentalAmount() {
        return rentalAmount;
    }
    
    public void setRentalAmount(BigDecimal rentalAmount) {
        this.rentalAmount = rentalAmount;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public BigDecimal getMembershipDiscount() {
        return membershipDiscount;
    }
    
    public void setMembershipDiscount(BigDecimal membershipDiscount) {
        this.membershipDiscount = membershipDiscount;
    }
    
    public BigDecimal getLongRentalDiscount() {
        return longRentalDiscount;
    }
    
    public void setLongRentalDiscount(BigDecimal longRentalDiscount) {
        this.longRentalDiscount = longRentalDiscount;
    }
    
    public BigDecimal getFinalPayableAmount() {
        return finalPayableAmount;
    }
    
    public void setFinalPayableAmount(BigDecimal finalPayableAmount) {
        this.finalPayableAmount = finalPayableAmount;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }
    
    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Rental{" +
                "rentalCode='" + rentalCode + '\'' +
                ", equipmentDetails='" + equipmentDetails + '\'' +
                ", customerName='" + customerName + '\'' +
                ", rentalStatus=" + rentalStatus +
                '}';
    }
}