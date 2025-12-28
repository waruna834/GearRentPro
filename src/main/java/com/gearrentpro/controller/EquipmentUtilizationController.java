package com.gearrentpro.controller;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.Rental;
import com.gearrentpro.entity.User;
import com.gearrentpro.service.AuthenticationService;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.service.EquipmentService;
import com.gearrentpro.service.RentalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EquipmentUtilizationController {
    
    @FXML
    private Label branchNameLabel;
    
    @FXML
    private DatePicker fromDatePicker;
    
    @FXML
    private DatePicker toDatePicker;
    
    @FXML
    private TableView<UtilizationRow> utilizationTable;
    
    @FXML
    private TableColumn<UtilizationRow, String> equipmentColumn;
    
    @FXML
    private TableColumn<UtilizationRow, String> categoryColumn;
    
    @FXML
    private TableColumn<UtilizationRow, String> rentedDaysColumn;
    
    @FXML
    private TableColumn<UtilizationRow, String> availableDaysColumn;
    
    @FXML
    private TableColumn<UtilizationRow, String> utilizationPercentColumn;
    
    private BranchService branchService;
    private EquipmentService equipmentService;
    private RentalService rentalService;
    
    @FXML
    public void initialize() {
        branchService = BranchService.getInstance();
        equipmentService = EquipmentService.getInstance();
        rentalService = RentalService.getInstance();

        // Show branch name for branch manager
        AuthenticationService authService = AuthenticationService.getInstance();
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == User.UserRole.BRANCH_MANAGER) {
            Integer branchId = currentUser.getBranchId();
            if (branchId != null) {
                try {
                    Branch branch = branchService.getBranchById(branchId);
                    if (branch != null && branchNameLabel != null) {
                        branchNameLabel.setText(branch.getBranchName());
                    }
                } catch (Exception e) {
                    if (branchNameLabel != null) branchNameLabel.setText("Branch: Unknown");
                }
            }
        }
        
        loadBranches();
        setupTableColumns();
    }
    
    /**
     * Load branches
     */
    private void loadBranches() {
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        equipmentColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().equipmentCode));
        categoryColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().category));
        rentedDaysColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().rentedDays)));
        availableDaysColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().availableDays)));
        utilizationPercentColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.2f%%", cellData.getValue().utilizationPercent)));
    }
    
    /**
     * Generate report
     */
    @FXML
    private void handleGenerateReport() {
        try {
            // Get branch for current branch manager
            AuthenticationService authService = AuthenticationService.getInstance();
            User currentUser = authService.getCurrentUser();
            Branch selectedBranch = null;
            if (currentUser != null && currentUser.getRole() == User.UserRole.BRANCH_MANAGER) {
                Integer branchId = currentUser.getBranchId();
                if (branchId != null) {
                    selectedBranch = branchService.getBranchById(branchId);
                }
            }
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();

            if (selectedBranch == null || fromDate == null || toDate == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields!");
                return;
            }

            if (toDate.isBefore(fromDate)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "End date must be after start date!");
                return;
            }

            // Get equipment for branch
            List<Equipment> equipmentList = equipmentService.getEquipmentByBranch(selectedBranch.getBranchId());
            List<Rental> allRentals = rentalService.getAllRentals();

            List<UtilizationRow> rows = equipmentList.stream().map(equip -> {
                // Calculate rented days for this equipment in date range
                long rentedDays = allRentals.stream()
                    .filter(r -> r.getEquipmentId() == equip.getEquipmentId() &&
                               r.getStartDate().isBefore(toDate) &&
                               r.getEndDate().isAfter(fromDate))
                    .mapToLong(r -> ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()))
                    .sum();

                long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
                double utilizationPercent = (rentedDays * 100.0) / totalDays;

                UtilizationRow row = new UtilizationRow();
                row.equipmentCode = equip.getEquipmentCode();
                row.category = equip.getCategoryName();
                row.rentedDays = (int) rentedDays;
                row.availableDays = (int) (totalDays - rentedDays);
                row.utilizationPercent = utilizationPercent;

                return row;
            }).collect(java.util.stream.Collectors.toList());

            utilizationTable.setItems(FXCollections.observableArrayList(rows));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report: " + e.getMessage());
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
    
    /**
     * Inner class for utilization row
     */
    public static class UtilizationRow {
        public String equipmentCode;
        public String category;
        public int rentedDays;
        public int availableDays;
        public double utilizationPercent;
    }
}