package com.gearrentpro.service;

import com.gearrentpro.dao.RentalDAO;
import com.gearrentpro.dao.ReservationDAO;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Rental;
import com.gearrentpro.entity.Reservation;
import com.gearrentpro.util.DateUtils;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReservationService {

  private static final int MAX_RENTAL_DAYS = 30;
  private static ReservationService instance;
  private final CategoryService categoryService;
  private final CustomerService customerService;
  private final EquipmentService equipmentService;
  private final PricingCalculationService pricingCalculationService;
  private final RentalDAO rentalDAO;
  private final ReservationDAO reservationDAO;

  private ReservationService() {
    this.reservationDAO = new ReservationDAO();
    this.rentalDAO = new RentalDAO();
    this.equipmentService = EquipmentService.getInstance();
    this.pricingCalculationService = PricingCalculationService.getInstance();
    this.customerService = CustomerService.getInstance();
    this.categoryService = CategoryService.getInstance();
  }

  public static ReservationService getInstance() {
    if (instance == null) {
      instance = new ReservationService();
    }
    return instance;
  }

  public boolean cancelReservation(int reservationId) throws SQLException {
    return reservationDAO.updateReservationStatus(
        reservationId, Reservation.ReservationStatus.CANCELLED);
  }

  public boolean convertToRental(int reservationId) throws Exception {
    Reservation reservation = getReservationById(reservationId);
    if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
      return false; // Or throw an exception
    }

    Equipment equipment = equipmentService.getEquipmentById(reservation.getEquipmentId());
    int days = DateUtils.getDaysBetween(reservation.getStartDate(), reservation.getEndDate());

    Rental rental = new Rental();
    rental.setRentalCode("RENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    rental.setEquipmentId(reservation.getEquipmentId());
    rental.setCustomerId(reservation.getCustomerId());
    rental.setBranchId(reservation.getBranchId());
    rental.setReservationId(reservation.getReservationId());
    rental.setStartDate(reservation.getStartDate());
    rental.setEndDate(reservation.getEndDate());
    rental.setDailyRate(equipment.getDailyBasePrice());

    Category category = categoryService.getCategoryById(equipment.getCategoryId());
    BigDecimal rentalAmount =
        pricingCalculationService.calculateRentalAmount(
            equipment, category, reservation.getStartDate(), reservation.getEndDate());
    rental.setRentalAmount(rentalAmount);
    rental.setSecurityDeposit(equipment.getSecurityDeposit());

    Customer customer = customerService.getCustomerById(reservation.getCustomerId());
    BigDecimal membershipDiscount =
        pricingCalculationService.calculateMembershipDiscount(rentalAmount, customer);
    rental.setMembershipDiscount(membershipDiscount);

    BigDecimal longRentalDiscount =
        pricingCalculationService.calculateLongRentalDiscount(
            rentalAmount, reservation.getStartDate(), reservation.getEndDate());
    rental.setLongRentalDiscount(longRentalDiscount);

    rental.setFinalPayableAmount(
        pricingCalculationService.calculateFinalPayableAmount(
            rentalAmount, longRentalDiscount, membershipDiscount));
    rental.setPaymentStatus(Rental.PaymentStatus.UNPAID);
    rental.setRentalStatus(Rental.RentalStatus.ACTIVE);

    if (rentalDAO.createRental(rental)) {
      return updateReservationStatus(reservationId, Reservation.ReservationStatus.CONFIRMED);
    }

    return false;
  }

  public boolean createReservation(Reservation reservation, int customerId) throws SQLException {
    validateReservation(reservation);

    // Check for overlapping reservations and rentals
    if (hasDateConflict(
        reservation.getEquipmentId(), reservation.getStartDate(), reservation.getEndDate())) {
      throw new IllegalArgumentException("Equipment not available for selected dates!");
    }

    // Check customer deposit limit
    if (!isCustomerDepositLimitValid(customerId, reservation)) {
      throw new IllegalArgumentException("Customer deposit limit would be exceeded!");
    }

    // Generate reservation code
    reservation.setReservationCode(
        "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

    int reservationId = reservationDAO.createReservation(reservation);
    return reservationId > 0;
  }

  public List<Reservation> getAllReservations() throws SQLException {
    return reservationDAO.getAllReservations();
  }

  public Reservation getReservationById(int reservationId) throws SQLException {
    return reservationDAO.getReservationById(reservationId);
  }

  public List<Reservation> getReservationsByBranch(int branchId) throws SQLException {
    return reservationDAO.getReservationsByBranch(branchId);
  }

  public boolean updateReservationStatus(int reservationId, Reservation.ReservationStatus status)
      throws SQLException {
    return reservationDAO.updateReservationStatus(reservationId, status);
  }

  private boolean hasDateConflict(int equipmentId, LocalDate startDate, LocalDate endDate)
      throws SQLException {
    // Check reservations
    if (reservationDAO.hasOverlappingReservation(equipmentId, startDate, endDate)) {
      return true;
    }

    // Check rentals (from RentalDAO)
    return rentalDAO.isEquipmentRented(equipmentId, startDate, endDate);
  }

  private boolean isCustomerDepositLimitValid(int customerId, Reservation reservation)
      throws SQLException {
    // This will be implemented when we add deposit calculation
    return true;
  }

  private void validateReservation(Reservation reservation) throws IllegalArgumentException {
    if (reservation.getStartDate() == null || reservation.getEndDate() == null) {
      throw new IllegalArgumentException("Start and end dates are required!");
    }

    if (reservation.getStartDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Reservation cannot be in the past!");
    }

    if (reservation.getEndDate().isBefore(reservation.getStartDate())) {
      throw new IllegalArgumentException("End date must be after start date!");
    }

    int days = DateUtils.getDaysBetween(reservation.getStartDate(), reservation.getEndDate());
    if (days > MAX_RENTAL_DAYS) {
      throw new IllegalArgumentException("Reservation duration cannot exceed 30 days!");
    }
  }
}