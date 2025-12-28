package com.gearrentpro.controller;

import com.gearrentpro.entity.*;
import com.gearrentpro.service.*;
import com.gearrentpro.dao.ReservationDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateRentalController {
    
    @FXML
    private ComboBox<Customer> customerCombo;
    
    @FXML
    private ComboBox<Category> categoryCombo;
    
    @FXML
    private ComboBox<Equipment> equipmentCombo;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private Label rentalAmountLabel;
    
    @FXML
    private Label discountLabel;
    
    @FXML
    private Label finalAmountLabel;
    
    @FXML
    private Label depositLabel;
    
    private RentalService rentalService;
    private CustomerService customerService;
    private EquipmentService equipmentService;
    private CategoryService categoryService;
    private PricingCalculationService pricingService;
    private AuthenticationService authService;
    private ReservationDAO reservationDAO;
    private ReservationService reservationService;

    @FXML
    public void initialize() {
        rentalService = RentalService.getInstance();
        customerService = CustomerService.getInstance();
        equipmentService = EquipmentService.getInstance();
        categoryService = CategoryService.getInstance();
        pricingService = PricingCalculationService.getInstance();
        authService = AuthenticationService.getInstance();
        reservationDAO = new ReservationDAO();
        reservationService = ReservationService.getInstance();
        
        loadCustomers();
        loadCategories();
        
        // Add listeners for date and selection changes
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculatePricing());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculatePricing());
        categoryCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadEquipment();
            calculatePricing();
        });
        equipmentCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> calculatePricing());
        customerCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> calculatePricing());
    }
    
    /**
     * Load customers
     */
    private void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            customerCombo.setItems(FXCollections.observableArrayList(customers));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load customers: " + e.getMessage());
        }
    }
    
    /**
     * Load categories
     */
    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            categoryCombo.setItems(FXCollections.observableArrayList(categories));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load categories: " + e.getMessage());
        }
    }
    
    /**
     * Load equipment by selected category
     */
    private void loadEquipment() {
        try {
            Category category = categoryCombo.getValue();
            if (category == null) return;
            
            Integer branchId = authService.getCurrentUserBranchId();
            if (branchId == null) branchId = 1; // Default to first branch for admin
            
            List<Equipment> equipment = equipmentService.getAvailableEquipment(branchId, category.getCategoryId());
            equipmentCombo.setItems(FXCollections.observableArrayList(equipment));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load equipment: " + e.getMessage());
        }
    }
    
    /**
     * Calculate rental pricing
     */
    private void calculatePricing() {
        try {
            Equipment equipment = equipmentCombo.getValue();
            Category category = categoryCombo.getValue();
            Customer customer = customerCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (equipment == null || category == null || customer == null || 
                startDate == null || endDate == null) {
                clearPricingLabels();
                return;
            }
            
            if (endDate.isBefore(startDate)) {
                clearPricingLabels();
                return;
            }
            
            // Calculate amounts
            BigDecimal rentalAmount = pricingService.calculateRentalAmount(equipment, category, startDate, endDate);
            BigDecimal longDiscount = pricingService.calculateLongRentalDiscount(rentalAmount, startDate, endDate);
            BigDecimal membershipDiscount = pricingService.calculateMembershipDiscount(rentalAmount, customer);
            BigDecimal finalAmount = pricingService.calculateFinalPayableAmount(rentalAmount, longDiscount, membershipDiscount);
            
            // Update labels
            rentalAmountLabel.setText("LKR " + rentalAmount);
            discountLabel.setText("LKR " + (longDiscount.add(membershipDiscount)));
            finalAmountLabel.setText("LKR " + finalAmount);
            depositLabel.setText("LKR " + equipment.getSecurityDeposit());
            
        } catch (Exception e) {
            clearPricingLabels();
        }
    }
    
    /**
     * Clear pricing labels
     */
    private void clearPricingLabels() {
        rentalAmountLabel.setText("LKR 0");
        discountLabel.setText("LKR 0");
        finalAmountLabel.setText("LKR 0");
        depositLabel.setText("LKR 0");
    }
    
    /**
     * Handle create rental - FIXED VERSION
     */
    @FXML
    private void handleCreateRental() {
        try {
            // Validation
            Equipment equipment = equipmentCombo.getValue();
            Category category = categoryCombo.getValue();
            Customer customer = customerCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (equipment == null || category == null || customer == null || startDate == null || endDate == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields!");
                return;
            }

            // Validate dates
            if (endDate.isBefore(startDate)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "End date must be after start date!");
                return;
            }

            int rentalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (rentalDays > 30) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Rental duration cannot exceed 30 days!");
                return;
            }

            // Equipment availability check
            if (!equipmentService.isEquipmentAvailable(equipment.getEquipmentId(), startDate, endDate)) {
                showAlert(Alert.AlertType.WARNING, "Equipment Unavailable", "The selected equipment is not available for the chosen dates.");
                return;
            }

            // Create reservation only (no rental)
            Reservation reservation = new Reservation();
            reservation.setReservationCode("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            reservation.setEquipmentId(equipment.getEquipmentId());
            reservation.setCustomerId(customer.getCustomerId());
            reservation.setBranchId(authService.getCurrentUserBranchId() != null ? authService.getCurrentUserBranchId() : 1);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setStatus(Reservation.ReservationStatus.PENDING);

            int reservationId = reservationDAO.createReservation(reservation);
            if (reservationId > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Reservation created successfully!\n" +
                        "For covert this to a rental or cancel, go to the reservations\n\n" +
                        "Reservation Code: " + reservation.getReservationCode() + "\n" +
                        "Customer: " + customer.getCustomerName() + "\n" +
                        "Equipment: " + equipment.getBrand() + " " + equipment.getModel() + "\n" +
                        "Dates: " + startDate + " to " + endDate);
                clearForm();
                loadCategories();
                loadCustomers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create reservation.");
            }

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create reservation: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear form
     */
    @FXML
    private void clearForm() {
        customerCombo.setValue(null);
        categoryCombo.setValue(null);
        equipmentCombo.setValue(null);
        equipmentCombo.getItems().clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        clearPricingLabels();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}