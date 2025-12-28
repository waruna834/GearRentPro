package com.gearrentpro.controller;

import com.gearrentpro.service.MembershipService;
import com.gearrentpro.entity.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class ConfigurationController {
    
    @FXML
    private TextField maxRentalDaysField;
    
    @FXML
    private TextField longRentalMinDaysField;
    
    @FXML
    private TextField longRentalDiscountField;
    
    @FXML
    private TextField lateFeeDayField;
    
    @FXML
    private TextField depositLimitField;
    
    @FXML
    private TextField regularDiscountField;
    
    @FXML
    private TextField silverDiscountField;
    
    @FXML
    private TextField goldDiscountField;
    
    private MembershipService membershipService;
    
    @FXML
    public void initialize() {
        membershipService = MembershipService.getInstance();
        loadCurrentSettings();
    }
    
    /**
     * Load current configuration settings
     */
    private void loadCurrentSettings() {
        // Default values (these would come from database in full implementation)
        maxRentalDaysField.setText("30");
        longRentalMinDaysField.setText("7");
        longRentalDiscountField.setText("10");
        lateFeeDayField.setText("500");
        depositLimitField.setText("500000");
        
        // Load membership discounts
        regularDiscountField.setText(membershipService.getDiscountPercentage(Customer.MembershipLevel.REGULAR).toString());
        silverDiscountField.setText(membershipService.getDiscountPercentage(Customer.MembershipLevel.SILVER).toString());
        goldDiscountField.setText(membershipService.getDiscountPercentage(Customer.MembershipLevel.GOLD).toString());
    }
    
    /**
     * Handle save settings button
     */
    @FXML
    private void handleSaveSettings() {
        try {
            // Validate inputs
            int maxDays = Integer.parseInt(maxRentalDaysField.getText());
            int minDays = Integer.parseInt(longRentalMinDaysField.getText());
            double longDiscount = Double.parseDouble(longRentalDiscountField.getText());
            double lateFee = Double.parseDouble(lateFeeDayField.getText());
            double depositLimit = Double.parseDouble(depositLimitField.getText());
            
            double regularDisc = Double.parseDouble(regularDiscountField.getText());
            double silverDisc = Double.parseDouble(silverDiscountField.getText());
            double goldDisc = Double.parseDouble(goldDiscountField.getText());
            
            // Validate ranges
            if (maxDays <= 0 || minDays <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Days must be greater than 0!");
                return;
            }
            
            if (longDiscount < 0 || longDiscount > 100) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Discount must be between 0-100!");
                return;
            }
            
            if (lateFee < 0 || depositLimit < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amounts must be positive!");
                return;
            }
            
            // Update membership discounts
            membershipService.setDiscountPercentage(Customer.MembershipLevel.REGULAR, BigDecimal.valueOf(regularDisc));
            membershipService.setDiscountPercentage(Customer.MembershipLevel.SILVER, BigDecimal.valueOf(silverDisc));
            membershipService.setDiscountPercentage(Customer.MembershipLevel.GOLD, BigDecimal.valueOf(goldDisc));
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                     "System configuration saved successfully!\n\n" +
                     "Max Rental: " + maxDays + " days\n" +
                     "Long Rental Min: " + minDays + " days\n" +
                     "Discounts - Regular: " + regularDisc + "%, Silver: " + silverDisc + "%, Gold: " + goldDisc + "%");
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers!");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        }
    }
    
    /**
     * Handle reset to defaults button
     */
    @FXML
    private void handleResetDefaults() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Reset to Defaults");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will reset all settings to default values.");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            maxRentalDaysField.setText("30");
            longRentalMinDaysField.setText("7");
            longRentalDiscountField.setText("10");
            lateFeeDayField.setText("500");
            depositLimitField.setText("500000");
            
            regularDiscountField.setText("0");
            silverDiscountField.setText("5");
            goldDiscountField.setText("10");
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Settings reset to defaults!");
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