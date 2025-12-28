package com.gearrentpro.entity;

import java.time.LocalDateTime;

public class Branch {
    private int branchId;
    private String branchCode;
    private String branchName;
    private String address;
    private String contactNumber;
    private String email;
    private LocalDateTime createdAt;
    
    // Constructor
    public Branch() {}
    
    public Branch(String branchCode, String branchName, String address, String contactNumber, String email) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }
    
    // Getters and Setters
    public int getBranchId() {
        return branchId;
    }
    
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    
    public String getBranchCode() {
        return branchCode;
    }
    
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return branchName + " (" + branchCode + ")";
    }
}