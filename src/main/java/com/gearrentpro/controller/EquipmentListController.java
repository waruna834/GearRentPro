package com.gearrentpro.controller;

import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Category;
import com.gearrentpro.service.EquipmentService;
import com.gearrentpro.service.CategoryService;
import com.gearrentpro.service.AuthenticationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EquipmentListController {
    
    @FXML
    private TableView<Equipment> equipmentTable;
    
    @FXML
    private TableColumn<Equipment, String> codeColumn;
    
    @FXML
    private TableColumn<Equipment, String> categoryColumn;
    
    @FXML
    private TableColumn<Equipment, String> brandColumn;
    
    @FXML
    private TableColumn<Equipment, String> modelColumn;
    
    @FXML
    private TableColumn<Equipment, String> priceColumn;
    
    @FXML
    private TableColumn<Equipment, String> statusColumn;
    
    @FXML
    private ComboBox<String> statusFilter;
    
    @FXML
    private TextField searchField;
    
    private EquipmentService equipmentService;
    private CategoryService categoryService;
    private AuthenticationService authService;
    private List<Equipment> allEquipment;
    
    @FXML
    public void initialize() {
        equipmentService = EquipmentService.getInstance();
        categoryService = CategoryService.getInstance();
        authService = AuthenticationService.getInstance();
        setupTableColumns();
        setupStatusFilter();
        loadEquipment();
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEquipmentCode()));
        categoryColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoryName()));
        brandColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBrand()));
        modelColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModel()));
        priceColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("LKR " + cellData.getValue().getDailyBasePrice()));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }
    
    /**
     * Setup status filter
     */
    private void setupStatusFilter() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "ALL", "AVAILABLE", "RESERVED", "RENTED", "UNDER_MAINTENANCE"
        ));
        statusFilter.setValue("ALL");
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterByStatus(newVal);
            }
        });
    }
    
    /**
     * Load all equipment
     */
    private void loadEquipment() {
        try {
            Integer branchId = authService.getCurrentUserBranchId();
            
            if (branchId != null && !authService.isAdmin()) {
                // Branch manager/staff sees only their branch equipment
                allEquipment = equipmentService.getEquipmentByBranch(branchId);
            } else {
                // Admin sees all equipment
                allEquipment = equipmentService.getAllEquipment();
            }
            
            ObservableList<Equipment> observableList = FXCollections.observableArrayList(allEquipment);
            equipmentTable.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load equipment: " + e.getMessage());
        }
    }
    
    /**
     * Filter equipment by status
     */
    private void filterByStatus(String status) {
        if ("ALL".equals(status)) {
            ObservableList<Equipment> observableList = FXCollections.observableArrayList(allEquipment);
            equipmentTable.setItems(observableList);
            return;
        }
        
        List<Equipment> filtered = allEquipment.stream()
            .filter(e -> e.getStatus().toString().equals(status))
            .collect(java.util.stream.Collectors.toList());
        
        ObservableList<Equipment> observableList = FXCollections.observableArrayList(filtered);
        equipmentTable.setItems(observableList);
    }
    
    /**
     * Handle search
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadEquipment();
            return;
        }
        
        List<Equipment> filtered = allEquipment.stream()
            .filter(e -> e.getEquipmentCode().toLowerCase().contains(searchText) ||
                        e.getBrand().toLowerCase().contains(searchText) ||
                        e.getModel().toLowerCase().contains(searchText))
            .collect(java.util.stream.Collectors.toList());
        
        ObservableList<Equipment> observableList = FXCollections.observableArrayList(filtered);
        equipmentTable.setItems(observableList);
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