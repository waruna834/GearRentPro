package com.gearrentpro.controller;

import com.gearrentpro.entity.Rental;
import com.gearrentpro.entity.ReturnDetails;
import com.gearrentpro.service.RentalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProcessReturnController {

    @FXML
    private ComboBox<Rental> rentalCombo;

    @FXML
    private Label rentalDetailsLabel;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TextArea damageDescriptionArea;

    @FXML
    private TextField damageChargeField;

    @FXML
    private Label lateFeeLabel;

    @FXML
    private Label totalChargesLabel;

    @FXML
    private Label refundLabel;

    private RentalService rentalService;

    @FXML
    public void initialize() {
        rentalService = RentalService.getInstance();
        loadActiveRentals();

        rentalCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showRentalDetails(newVal);
            }
        });

        returnDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateCharges());
        damageChargeField.textProperty().addListener((obs, oldVal, newVal) -> calculateCharges());
    }

    /**
     * Load active rentals
     */
    private void loadActiveRentals() {
        try {
            // Get all rentals and filter for active ones
            List<Rental> allRentals = rentalService.getAllRentals();
            List<Rental> activeRentals = allRentals.stream()
                    .filter(r -> r.getRentalStatus() == Rental.RentalStatus.ACTIVE ||
                            r.getRentalStatus() == Rental.RentalStatus.OVERDUE)
                    .collect(java.util.stream.Collectors.toList());

            rentalCombo.setItems(FXCollections.observableArrayList(activeRentals));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rentals: " + e.getMessage());
        }
    }

    /**
     * Show rental details
     */
    private void showRentalDetails(Rental rental) {
        String details = "Rental Code: " + rental.getRentalCode() + "\n" +
                "Equipment: " + rental.getEquipmentDetails() + "\n" +
                "Customer: " + rental.getCustomerName() + "\n" +
                "Start Date: " + rental.getStartDate() + "\n" +
                "End Date: " + rental.getEndDate() + "\n" +
                "Security Deposit: LKR " + rental.getSecurityDeposit() + "\n" +
                "Final Amount: LKR " + rental.getFinalPayableAmount();

        rentalDetailsLabel.setText(details);
        returnDatePicker.setValue(null);
        damageDescriptionArea.clear();
        damageChargeField.clear();
    }

    /**
     * Calculate late fees and charges
     */
    private void calculateCharges() {
        try {
            Rental rental = rentalCombo.getValue();
            LocalDate returnDate = returnDatePicker.getValue();

            if (rental == null || returnDate == null) {
                clearChargeLabels();
                return;
            }

            BigDecimal damageCharge = BigDecimal.ZERO;
            if (!damageChargeField.getText().isEmpty()) {
                try {
                    damageCharge = new BigDecimal(damageChargeField.getText());
                } catch (NumberFormatException e) {
                    // Invalid number
                }
            }

            // Calculate late fee (assuming 500 per day as default)
            BigDecimal lateFee = BigDecimal.ZERO;
            if (returnDate.isAfter(rental.getEndDate())) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
                lateFee = new BigDecimal(500).multiply(BigDecimal.valueOf(days));
            }

            BigDecimal totalCharges = lateFee.add(damageCharge);
            BigDecimal refund = rental.getSecurityDeposit().subtract(totalCharges);

            if (refund.compareTo(BigDecimal.ZERO) < 0) {
                refund = BigDecimal.ZERO;
            }

            lateFeeLabel.setText("LKR " + lateFee);
            totalChargesLabel.setText("LKR " + totalCharges);
            refundLabel.setText("LKR " + refund);

        } catch (Exception e) {
            clearChargeLabels();
        }
    }

    /**
     * Clear charge labels
     */
    private void clearChargeLabels() {
        lateFeeLabel.setText("LKR 0");
        totalChargesLabel.setText("LKR 0");
        refundLabel.setText("LKR 0");
    }

    /**
     * Handle clear form button
     */
    @FXML
    private void clearForm() {
        rentalCombo.setValue(null);
        returnDatePicker.setValue(null);
        damageDescriptionArea.clear();
        damageChargeField.clear();
        rentalDetailsLabel.setText("");
        clearChargeLabels();
    }

    /**
     * Handle process return
     */
    @FXML
    private void handleProcessReturn() {
        try {
            Rental rental = rentalCombo.getValue();
            LocalDate returnDate = returnDatePicker.getValue();

            if (rental == null || returnDate == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select rental and return date!");
                return;
            }

            BigDecimal damageCharge = BigDecimal.ZERO;
            if (!damageChargeField.getText().isEmpty()) {
                try {
                    damageCharge = new BigDecimal(damageChargeField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid damage charge amount!");
                    return;
                }
            }

            String damageDesc = damageDescriptionArea.getText();

            // Process return
            ReturnDetails returnDetails = rentalService.processReturn(
                    rental.getRentalId(),
                    returnDate,
                    damageDesc,
                    damageCharge);

            rentalService.updatePaymentStatus(rental.getRentalId(), Rental.PaymentStatus.PAID);

            String message = "Return processed successfully!\n\n" +
                    "Late Fee: LKR " + returnDetails.getLateFee() + "\n" +
                    "Damage Charge: LKR " + returnDetails.getDamageCharge() + "\n" +
                    "Total Charges: LKR " + returnDetails.getTotalCharges() + "\n" +
                    "Refund Amount: LKR " + returnDetails.getRefundAmount() + "\n" +
                    "Additional Payment: LKR " + returnDetails.getAdditionalPaymentRequired();

            showAlert(Alert.AlertType.INFORMATION, "Success", message);
            clearForm();
            loadActiveRentals();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to process return: " + e.getMessage());
        }
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