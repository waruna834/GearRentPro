package com.gearrentpro.controller;

import com.gearrentpro.entity.Rental;
import com.gearrentpro.service.AuthenticationService;
import com.gearrentpro.service.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RentalListController {
    
    @FXML
    private TableView<Rental> rentalTable;
    
    @FXML
    private TableColumn<Rental, String> codeColumn;
    
    @FXML
    private TableColumn<Rental, String> equipmentColumn;
    
    @FXML
    private TableColumn<Rental, String> customerColumn;
    
    @FXML
    private TableColumn<Rental, String> statusColumn;
    
    @FXML
    private TableColumn<Rental, String> startDateColumn;
    
    @FXML
    private TableColumn<Rental, String> endDateColumn;
    
    @FXML
    private ComboBox<Rental.RentalStatus> statusFilter;
    
    private RentalService rentalService;
    private AuthenticationService authService;
    private List<Rental> allRentals;
    
    @FXML
    public void initialize() {
        rentalService = RentalService.getInstance();
        authService = AuthenticationService.getInstance();
        setupTableColumns();
        setupStatusFilter();
        loadRentals();
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRentalCode()));
        equipmentColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEquipmentDetails()));
        customerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRentalStatus().toString()));
        startDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDate().toString()));
    }
    
    /**
     * Setup status filter
     */
    private void setupStatusFilter() {
        statusFilter.setItems(FXCollections.observableArrayList(Rental.RentalStatus.values()));
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterByStatus(newVal);
            }
        });
    }
    
    /**
     * Load all rentals
     */
    private void loadRentals() {
        try {
            Integer branchId = authService.getCurrentUserBranchId();
            if (branchId != null) {
                // Branch user, load rentals for the specific branch
                allRentals = rentalService.getRentalsByBranch(branchId);
            } else {
                // Admin user, load all rentals
                allRentals = rentalService.getAllRentals();
            }
            ObservableList<Rental> observableList = FXCollections.observableArrayList(allRentals);
            rentalTable.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load rentals: " + e.getMessage());
        }
    }
    
    /**
     * Filter rentals by status
     */
    private void filterByStatus(Rental.RentalStatus status) {
        List<Rental> filtered = allRentals.stream()
            .filter(r -> r.getRentalStatus() == status)
            .collect(Collectors.toList());
        
        ObservableList<Rental> observableList = FXCollections.observableArrayList(filtered);
        rentalTable.setItems(observableList);
    }
    
    /**
     * Handle view rental details
     */
    @FXML
    private void handleViewDetails() {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a rental!");
            return;
        }
        
        String details = "Rental Code: " + selected.getRentalCode() + "\n" +
                        "Equipment: " + selected.getEquipmentDetails() + "\n" +
                        "Customer: " + selected.getCustomerName() + "\n" +
                        "Start Date: " + selected.getStartDate() + "\n" +
                        "End Date: " + selected.getEndDate() + "\n" +
                        "Daily Rate: " + selected.getDailyRate() + "\n" +
                        "Rental Amount: " + selected.getRentalAmount() + "\n" +
                        "Security Deposit: " + selected.getSecurityDeposit() + "\n" +
                        "Final Payable: " + selected.getFinalPayableAmount() + "\n" +
                        "Status: " + selected.getRentalStatus() + "\n" +
                        "Payment Status: " + selected.getPaymentStatus();
        
        showAlert(Alert.AlertType.INFORMATION, "Rental Details", details);
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