package com.gearrentpro.controller;

import com.gearrentpro.entity.Rental;
import com.gearrentpro.service.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class OverdueRentalsController {
    
    @FXML
    private TableView<Rental> overdueTable;
    
    @FXML
    private TableColumn<Rental, String> rentalCodeColumn;
    
    @FXML
    private TableColumn<Rental, String> customerColumn;
    
    @FXML
    private TableColumn<Rental, String> equipmentColumn;
    
    @FXML
    private TableColumn<Rental, String> dueDateColumn;
    
    @FXML
    private TableColumn<Rental, String> daysOverdueColumn;
    
    @FXML
    private TableColumn<Rental, String> phoneColumn;
    
    private RentalService rentalService;
    
    @FXML
    public void initialize() {
        rentalService = RentalService.getInstance();
        setupTableColumns();
        loadOverdueRentals();
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        rentalCodeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRentalCode()));
        customerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));
        equipmentColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEquipmentDetails()));
        dueDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        daysOverdueColumn.setCellValueFactory(cellData -> {
            long days = ChronoUnit.DAYS.between(cellData.getValue().getEndDate(), LocalDate.now());
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(days));
        });
        phoneColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("Contact"));
    }
    
    /**
     * Load overdue rentals
     */
    private void loadOverdueRentals() {
        try {
            List<Rental> overdueRentals = rentalService.getOverdueRentals();
            ObservableList<Rental> observableList = FXCollections.observableArrayList(overdueRentals);
            overdueTable.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load overdue rentals: " + e.getMessage());
        }
    }
    
    /**
     * Handle contact customer
     */
    @FXML
    private void handleContactCustomer() {
        Rental selected = overdueTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a rental!");
            return;
        }
        
        String message = "Customer: " + selected.getCustomerName() + "\n" +
                        "Phone: [Contact info here]\n" +
                        "Equipment: " + selected.getEquipmentDetails() + "\n" +
                        "Days Overdue: " + ChronoUnit.DAYS.between(selected.getEndDate(), LocalDate.now());
        
        showAlert(Alert.AlertType.INFORMATION, "Customer Contact", message);
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