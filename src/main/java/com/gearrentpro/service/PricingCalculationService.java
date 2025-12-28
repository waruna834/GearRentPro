package com.gearrentpro.service;

import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import com.gearrentpro.service.MembershipService;

public class PricingCalculationService {
    
    private static PricingCalculationService instance;
    private static final BigDecimal LONG_RENTAL_MIN_DAYS = BigDecimal.valueOf(7);
    private static final BigDecimal LONG_RENTAL_DISCOUNT_PERCENT = BigDecimal.valueOf(10);
    
    private PricingCalculationService() {}
    
    public static PricingCalculationService getInstance() {
        if (instance == null) {
            instance = new PricingCalculationService();
        }
        return instance;
    }
    
    /**
     * Calculate daily rental rate considering category factor and weekend multiplier
     */
    public BigDecimal calculateDailyRate(Equipment equipment, Category category, LocalDate rentalDate) {
        BigDecimal dailyRate = equipment.getDailyBasePrice();
        
        // Apply category factor
        dailyRate = dailyRate.multiply(category.getBasePriceFactor());
        
        // Apply weekend multiplier if applicable
        if (DateUtils.isWeekend(rentalDate)) {
            dailyRate = dailyRate.multiply(category.getWeekendMultiplier());
        }
        
        return dailyRate.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate total rental amount with weekend considerations
     */
    public BigDecimal calculateRentalAmount(Equipment equipment, Category category, 
                                           LocalDate startDate, LocalDate endDate) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDate current = startDate;
        
        // Iterate through each day and add appropriate daily rate
        while (!current.isAfter(endDate)) {
            BigDecimal dailyRate = calculateDailyRate(equipment, category, current);
            totalAmount = totalAmount.add(dailyRate);
            current = current.plusDays(1);
        }
        
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate long rental discount (if rental >= 7 days)
     */
    public BigDecimal calculateLongRentalDiscount(BigDecimal rentalAmount, 
                                                  LocalDate startDate, LocalDate endDate) {
        int rentalDays = DateUtils.getDaysBetween(startDate, endDate);
        
        if (rentalDays >= LONG_RENTAL_MIN_DAYS.intValue()) {
            return rentalAmount.multiply(LONG_RENTAL_DISCOUNT_PERCENT)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate membership discount based on customer membership level
     */
    public BigDecimal calculateMembershipDiscount(BigDecimal rentalAmount, Customer customer) throws Exception {
        if (customer.getMembershipLevel() == Customer.MembershipLevel.REGULAR) {
            return BigDecimal.ZERO;
        }
        
        // Get membership discount percentage from service
        MembershipService membershipService = MembershipService.getInstance();
        BigDecimal discountPercent = membershipService.getDiscountPercentage(customer.getMembershipLevel());
        
        return rentalAmount.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate final payable amount (rental - discounts)
     */
    public BigDecimal calculateFinalPayableAmount(BigDecimal rentalAmount, 
                                                 BigDecimal longRentalDiscount,
                                                 BigDecimal membershipDiscount) {
        BigDecimal payable = rentalAmount.subtract(longRentalDiscount).subtract(membershipDiscount);
        return payable.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate late fees
     */
    public BigDecimal calculateLateFee(LocalDate endDate, LocalDate actualReturnDate, 
                                      BigDecimal dailyLateFee) {
        if (actualReturnDate.isAfter(endDate)) {
            int lateDays = (int) java.time.temporal.ChronoUnit.DAYS.between(endDate, actualReturnDate);
            return dailyLateFee.multiply(BigDecimal.valueOf(lateDays));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate refund or additional payment
     */
    public RefundInfo calculateRefundOrPayment(BigDecimal deposit, BigDecimal lateFee, 
                                              BigDecimal damageCharge) {
        BigDecimal totalCharges = lateFee.add(damageCharge);
        BigDecimal refund = BigDecimal.ZERO;
        BigDecimal additionalPayment = BigDecimal.ZERO;
        
        if (deposit.compareTo(totalCharges) >= 0) {
            refund = deposit.subtract(totalCharges);
        } else {
            additionalPayment = totalCharges.subtract(deposit);
        }
        
        return new RefundInfo(refund, additionalPayment, totalCharges);
    }
    
    /**
     * Inner class for refund information
     */
    public static class RefundInfo {
        public BigDecimal refundAmount;
        public BigDecimal additionalPayment;
        public BigDecimal totalCharges;
        
        public RefundInfo(BigDecimal refund, BigDecimal payment, BigDecimal charges) {
            this.refundAmount = refund;
            this.additionalPayment = payment;
            this.totalCharges = charges;
        }
    }
}