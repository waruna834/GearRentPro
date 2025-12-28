package com.gearrentpro.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\d{10}$");
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number (10 digits)
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Check if string is empty or null
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate NIC/Passport format
     */
    public static boolean isValidNIC(String nic) {
        return !isNullOrEmpty(nic) && nic.length() >= 9;
    }
    
    /**
     * Validate discount percentage (0-100)
     */
    public static boolean isValidDiscountPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }
    
    /**
     * Validate positive amount
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }
}