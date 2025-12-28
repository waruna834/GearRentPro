package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Customer {
    private int customerId;
    private String customerCode;
    private String customerName;
    private String nicPassport;
    private String contactNumber;
    private String email;
    private String address;
    private MembershipLevel membershipLevel;
    private BigDecimal depositLimit;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    
    public enum MembershipLevel {
        REGULAR, SILVER, GOLD
    }
    
    public enum CustomerStatus {
        ACTIVE, INACTIVE
    }
    
    // Constructor
    public Customer() {}
    
    public Customer(String customerCode, String customerName, String nicPassport, 
                    String contactNumber, String email, String address) {
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.nicPassport = nicPassport;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.membershipLevel = MembershipLevel.REGULAR;
        this.depositLimit = new BigDecimal("500000.00");
        this.status = CustomerStatus.ACTIVE;
    }
    
    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerCode() {
        return customerCode;
    }
    
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getNicPassport() {
        return nicPassport;
    }
    
    public void setNicPassport(String nicPassport) {
        this.nicPassport = nicPassport;
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }
    
    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
    
    public BigDecimal getDepositLimit() {
        return depositLimit;
    }
    
    public void setDepositLimit(BigDecimal depositLimit) {
        this.depositLimit = depositLimit;
    }
    
    public CustomerStatus getStatus() {
        return status;
    }
    
    public void setStatus(CustomerStatus status) {
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
        return customerName + " (" + customerCode + ") - " + membershipLevel;
    }
}