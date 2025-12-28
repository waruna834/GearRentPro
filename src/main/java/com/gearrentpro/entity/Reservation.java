package com.gearrentpro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private String reservationCode;
    private int equipmentId;
    private String equipmentDetails;
    private int customerId;
    private String customerName;
    private int branchId;
    private String branchName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED
    }
    
    // Constructor
    public Reservation() {}
    
    public Reservation(String reservationCode, int equipmentId, int customerId, 
                       int branchId, LocalDate startDate, LocalDate endDate) {
        this.reservationCode = reservationCode;
        this.equipmentId = equipmentId;
        this.customerId = customerId;
        this.branchId = branchId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ReservationStatus.PENDING;
    }
    
    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getReservationCode() {
        return reservationCode;
    }
    
    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
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
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
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
        return "Reservation{" +
                "reservationCode='" + reservationCode + '\'' +
                ", equipmentDetails='" + equipmentDetails + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                '}';
    }
}