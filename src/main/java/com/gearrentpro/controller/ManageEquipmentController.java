package com.gearrentpro.controller;

import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Branch;
import com.gearrentpro.service.EquipmentService;
import com.gearrentpro.service.CategoryService;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.service.AuthenticationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ManageEquipmentController {
    
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
    private HBox actionButtonsBox;
    
    private EquipmentService equipmentService;
    private CategoryService categoryService;
    private BranchService branchService;
    private AuthenticationService authService;
    
    @FXML
    public void initialize() {
        equipmentService = EquipmentService.getInstance();
        categoryService = CategoryService.getInstance();
        branchService = BranchService.getInstance();
        authService = AuthenticationService.getInstance();
        
        setupTableColumns();
        loadEquipment();
        
        // Hide action buttons if not admin
        if (!authService.isAdmin()) {
            actionButtonsBox.setVisible(false);
            actionButtonsBox.setManaged(false);
        }
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
     * Load all equipment
     */
    private void loadEquipment() {
        try {
            List<Equipment> equipment = equipmentService.getAllEquipment();
            equipmentTable.setItems(FXCollections.observableArrayList(equipment));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load equipment: " + e.getMessage());
        }
    }
    
    /**
     * Handle add equipment
     */
    @FXML
    private void handleAddEquipment() {
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Add New Equipment");
        dialog.setHeaderText("Create new equipment");
        
        TextField codeField = new TextField();
        codeField.setPromptText("Equipment Code");
        TextField brandField = new TextField();
        brandField.setPromptText("Brand");
        TextField modelField = new TextField();
        modelField.setPromptText("Model");
        TextField yearField = new TextField();
        yearField.setPromptText("Purchase Year");
        TextField priceField = new TextField();
        priceField.setPromptText("Daily Base Price");
        TextField depositField = new TextField();
        depositField.setPromptText("Security Deposit");
        
        ComboBox<Category> categoryCombo = new ComboBox<>();
        ComboBox<Branch> branchCombo = new ComboBox<>();
        ComboBox<Equipment.EquipmentStatus> statusCombo = new ComboBox<>();
        
        try {
            List<Category> categories = categoryService.getAllCategories();
            categoryCombo.setItems(FXCollections.observableArrayList(categories));
            
            List<Branch> branches = branchService.getAllBranches();
            branchCombo.setItems(FXCollections.observableArrayList(branches));
            
            statusCombo.setItems(FXCollections.observableArrayList(Equipment.EquipmentStatus.values()));
            statusCombo.setValue(Equipment.EquipmentStatus.AVAILABLE);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data!");
        }
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Equipment Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Brand:"), 0, 1);
        grid.add(brandField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);
        grid.add(modelField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Daily Price:"), 0, 5);
        grid.add(priceField, 1, 5);
        grid.add(new Label("Deposit:"), 0, 6);
        grid.add(depositField, 1, 6);
        grid.add(new Label("Branch:"), 0, 7);
        grid.add(branchCombo, 1, 7);
        grid.add(new Label("Status:"), 0, 8);
        grid.add(statusCombo, 1, 8);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    Equipment equip = new Equipment();
                    equip.setEquipmentCode(codeField.getText());
                    equip.setBrand(brandField.getText());
                    equip.setModel(modelField.getText());
                    equip.setPurchaseYear(Integer.parseInt(yearField.getText()));
                    equip.setDailyBasePrice(new BigDecimal(priceField.getText()));
                    equip.setSecurityDeposit(new BigDecimal(depositField.getText()));
                    equip.setCategoryId(categoryCombo.getValue().getCategoryId());
                    equip.setBranchId(branchCombo.getValue().getBranchId());
                    equip.setStatus(statusCombo.getValue());
                    return equip;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers!");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(equip -> {
            try {
                if (equipmentService.createEquipment(equip)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment added successfully!");
                    loadEquipment();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add equipment: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    /**
     * Handle update equipment
     */
    @FXML
    private void handleUpdateEquipment() {
        Equipment selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select equipment to update!");
            return;
        }
        
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Update Equipment");
        dialog.setHeaderText("Edit equipment information");
        
        TextField codeField = new TextField();
        codeField.setText(selected.getEquipmentCode());
        codeField.setDisable(true);
        
        TextField brandField = new TextField();
        brandField.setText(selected.getBrand());
        TextField modelField = new TextField();
        modelField.setText(selected.getModel());
        TextField yearField = new TextField();
        yearField.setText(String.valueOf(selected.getPurchaseYear()));
        TextField priceField = new TextField();
        priceField.setText(selected.getDailyBasePrice().toString());
        TextField depositField = new TextField();
        depositField.setText(selected.getSecurityDeposit().toString());
        
        ComboBox<Equipment.EquipmentStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(Equipment.EquipmentStatus.values()));
        statusCombo.setValue(selected.getStatus());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Equipment Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Brand:"), 0, 1);
        grid.add(brandField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);
        grid.add(modelField, 1, 2);
        grid.add(new Label("Year:"), 0, 3);
        grid.add(yearField, 1, 3);
        grid.add(new Label("Daily Price:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Deposit:"), 0, 5);
        grid.add(depositField, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == updateButton) {
                try {
                    selected.setBrand(brandField.getText());
                    selected.setModel(modelField.getText());
                    selected.setPurchaseYear(Integer.parseInt(yearField.getText()));
                    selected.setDailyBasePrice(new BigDecimal(priceField.getText()));
                    selected.setSecurityDeposit(new BigDecimal(depositField.getText()));
                    selected.setStatus(statusCombo.getValue());
                    return selected;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers!");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(equip -> {
            try {
                if (equipmentService.updateEquipment(equip)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment updated successfully!");
                    loadEquipment();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update equipment: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    /**
     * Handle delete equipment
     */
    @FXML
    private void handleDeleteEquipment() {
        Equipment selected = equipmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select equipment to delete!");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Equipment");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Delete: " + selected.getEquipmentCode() + " - " + 
                                    selected.getBrand() + " " + selected.getModel() + 
                                    "\n\nThis action cannot be undone!");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment deleted successfully!");
                loadEquipment();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete equipment: " + e.getMessage());
            }
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