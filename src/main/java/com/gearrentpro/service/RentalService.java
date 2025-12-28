package com.gearrentpro.service;

import com.gearrentpro.dao.RentalDAO;
import com.gearrentpro.entity.*;
import com.gearrentpro.util.DateUtils;
import com.gearrentpro.dao.ReturnDetailsDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentalService {

    private RentalDAO rentalDAO;
    private PricingCalculationService pricingService;
    private static RentalService instance;
    private static final int MAX_RENTAL_DAYS = 30;

    private RentalService() {
        this.rentalDAO = new RentalDAO();
        this.pricingService = PricingCalculationService.getInstance();
    }

    public static RentalService getInstance() {
        if (instance == null) {
            instance = new RentalService();
        }
        return instance;
    }

    /**
     * Get all rentals
     */
    public List<Rental> getAllRentals() throws SQLException {
        return rentalDAO.getAllRentals();
    }

    /**
     * Get rental by ID
     */
    public Rental getRentalById(int rentalId) throws SQLException {
        return rentalDAO.getRentalById(rentalId);
    }

    /**
     * Get active rentals by customer
     */
    public List<Rental> getActiveRentalsByCustomer(int customerId) throws SQLException {
        return rentalDAO.getActiveRentalsByCustomer(customerId);
    }

    /**
     * Get overdue rentals
     */
    public List<Rental> getOverdueRentals() throws SQLException {
        return rentalDAO.getOverdueRentals();
    }

    public List<Rental> getRentalsByBranch(int branchId) throws SQLException {
        return rentalDAO.getRentalsByBranch(branchId);
    }

    public boolean updatePaymentStatus(int rentalId, Rental.PaymentStatus status) throws SQLException {
        return rentalDAO.updatePaymentStatus(rentalId, status);
    }

    public Rental getRentalByReservationId(int reservationId) throws SQLException {
        return rentalDAO.getRentalByReservationId(reservationId);
    }

    public boolean updateRentalWithReservationId(int rentalId, int reservationId) throws SQLException {
        return rentalDAO.updateRentalWithReservationId(rentalId, reservationId);
    }

    public boolean updateRentalStatus(int rentalId, Rental.RentalStatus status) throws SQLException {
        return rentalDAO.updateRentalStatus(rentalId, status);
    }

    /**
     * Create rental with pricing calculation
     */
    public boolean createRental(Rental rental, Equipment equipment,
            Customer customer, Category category) throws SQLException, Exception {
        validateRental(rental);

        // Check equipment availability
        if (rentalDAO.isEquipmentRented(equipment.getEquipmentId(), rental.getStartDate(), rental.getEndDate())) {
            throw new IllegalArgumentException("Equipment is not available for selected dates!");
        }

        // Check customer deposit limit
        checkCustomerDepositLimit(customer, equipment.getSecurityDeposit());

        // Calculate pricing
        BigDecimal rentalAmount = pricingService.calculateRentalAmount(equipment, category,
                rental.getStartDate(), rental.getEndDate());
        BigDecimal longDiscount = pricingService.calculateLongRentalDiscount(rentalAmount,
                rental.getStartDate(), rental.getEndDate());
        BigDecimal membershipDiscount = pricingService.calculateMembershipDiscount(rentalAmount, customer);
        BigDecimal finalPayable = pricingService.calculateFinalPayableAmount(rentalAmount,
                longDiscount, membershipDiscount);

        // Set calculated values
        rental.setRentalCode("RENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        rental.setDailyRate(equipment.getDailyBasePrice().multiply(category.getBasePriceFactor()));
        rental.setRentalAmount(rentalAmount);
        rental.setSecurityDeposit(equipment.getSecurityDeposit());
        rental.setLongRentalDiscount(longDiscount);
        rental.setMembershipDiscount(membershipDiscount);
        rental.setFinalPayableAmount(finalPayable);
        rental.setPaymentStatus(Rental.PaymentStatus.UNPAID);
        rental.setRentalStatus(Rental.RentalStatus.ACTIVE);

        return rentalDAO.createRental(rental);
    }

    /**
     * Process rental return
     */
    public ReturnDetails processReturn(int rentalId, LocalDate actualReturnDate,
            String damageDescription, BigDecimal damageCharge) throws SQLException {
        Rental rental = rentalDAO.getRentalById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Rental not found!");
        }

        // Get category for late fee
        EquipmentService equipmentService = EquipmentService.getInstance();
        Equipment equipment = equipmentService.getEquipmentById(rental.getEquipmentId());
        CategoryService categoryService = CategoryService.getInstance();
        Category category = categoryService.getCategoryById(equipment.getCategoryId());

        // Calculate charges
        BigDecimal lateFee = pricingService.calculateLateFee(rental.getEndDate(), actualReturnDate,
                category.getDefaultLateFee());

        // Calculate refund/payment
        PricingCalculationService.RefundInfo refundInfo = pricingService.calculateRefundOrPayment(
                rental.getSecurityDeposit(), lateFee, damageCharge);

        // Create return details
        ReturnDetails returnDetails = new ReturnDetails(rentalId);
        returnDetails.setDamageDescription(damageDescription);
        returnDetails.setDamageCharge(damageCharge);
        returnDetails.setLateFee(lateFee);
        returnDetails.setTotalCharges(refundInfo.totalCharges);
        returnDetails.setRefundAmount(refundInfo.refundAmount);
        returnDetails.setAdditionalPaymentRequired(refundInfo.additionalPayment);

        // Update rental with return info
        rentalDAO.updateRentalReturn(rentalId, actualReturnDate, Rental.RentalStatus.RETURNED);

        // Save return details
        ReturnDetailsDAO returnDetailsDAO = new ReturnDetailsDAO();
        returnDetailsDAO.createReturnDetails(returnDetails);

        return returnDetails;
    }

    /**
     * Validate rental data
     */
    private void validateRental(Rental rental) throws IllegalArgumentException {
        if (rental.getStartDate() == null || rental.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required!");
        }

        if (rental.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rental cannot start in the past!");
        }

        if (rental.getEndDate().isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date!");
        }

        int days = DateUtils.getDaysBetween(rental.getStartDate(), rental.getEndDate());
        if (days > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException("Rental duration cannot exceed 30 days!");
        }
    }

    private void checkCustomerDepositLimit(Customer customer, BigDecimal newDeposit) throws SQLException {
        List<Rental> activeRentals = getActiveRentalsByCustomer(customer.getCustomerId());
        BigDecimal currentDeposit = activeRentals.stream()
                .map(Rental::getSecurityDeposit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (currentDeposit.add(newDeposit).compareTo(customer.getDepositLimit()) > 0) {
            throw new IllegalArgumentException("Customer deposit limit would be exceeded!");
        }
    }
}