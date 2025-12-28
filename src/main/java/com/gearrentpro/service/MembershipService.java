package com.gearrentpro.service;

import com.gearrentpro.entity.Customer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MembershipService {
    
    private static MembershipService instance;
    private Map<Customer.MembershipLevel, BigDecimal> discountMap;
    
    private MembershipService() {
        initializeDiscounts();
    }
    
    public static MembershipService getInstance() {
        if (instance == null) {
            instance = new MembershipService();
        }
        return instance;
    }
    
    /**
     * Initialize default membership discounts
     */
    private void initializeDiscounts() {
        discountMap = new HashMap<>();
        discountMap.put(Customer.MembershipLevel.REGULAR, BigDecimal.ZERO);
        discountMap.put(Customer.MembershipLevel.SILVER, BigDecimal.valueOf(5));
        discountMap.put(Customer.MembershipLevel.GOLD, BigDecimal.valueOf(10));
    }
    
    /**
     * Get discount percentage for membership level
     */
    public BigDecimal getDiscountPercentage(Customer.MembershipLevel level) {
        return discountMap.getOrDefault(level, BigDecimal.ZERO);
    }
    
    /**
     * Update membership discount percentage
     */
    public void setDiscountPercentage(Customer.MembershipLevel level, BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || 
            percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100!");
        }
        discountMap.put(level, percentage);
    }
}