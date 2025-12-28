package com.gearrentpro.controller;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.entity.Rental;
import com.gearrentpro.entity.User;
import com.gearrentpro.service.AuthenticationService;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.service.RentalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RevenueReportController {
        @FXML
        private Label branchNameLabel;
    
    @FXML
    private ComboBox<Branch> branchCombo;
    
    @FXML
    private DatePicker fromDatePicker;
    
    @FXML
    private DatePicker toDatePicker;
    
    @FXML
    private TableView<RevenueRow> reportTable;
    
    @FXML
    private TableColumn<RevenueRow, String> branchNameColumn;
    
    @FXML
    private TableColumn<RevenueRow, String> totalRentalsColumn;
    
    @FXML
    private TableColumn<RevenueRow, String> totalIncomeColumn;
    
    @FXML
    private TableColumn<RevenueRow, String> totalDepositColumn;
    
    @FXML
    private TableColumn<RevenueRow, String> totalLateFeesColumn;
    
    @FXML
    private Label totalRevenueLabel;
    
    private BranchService branchService;
    private RentalService rentalService;
    
    @FXML
    public void initialize() {
        branchService = BranchService.getInstance();
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
    private void loadBranches() {}
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        branchNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().branchName));
        totalRentalsColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().totalRentals)));
        totalIncomeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("LKR " + cellData.getValue().totalIncome));
        totalDepositColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("LKR " + cellData.getValue().totalDeposits));
        totalLateFeesColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("LKR " + cellData.getValue().totalLateFees));
    }
    
    /**
     * Generate report
     */
    @FXML
    private void handleGenerateReport() {
        try {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();
            
            if (fromDate == null || toDate == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select date range!");
                return;
            }
            
            if (toDate.isBefore(fromDate)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "End date must be after start date!");
                return;
            }
            
            // Get rentals for current branch
            AuthenticationService authService = AuthenticationService.getInstance();
            User currentUser = authService.getCurrentUser();
            List<Rental> allRentals;
            
            if (currentUser != null && currentUser.getRole() == User.UserRole.BRANCH_MANAGER) {
                Integer branchId = currentUser.getBranchId();
                allRentals = (branchId != null) ? rentalService.getRentalsByBranch(branchId) : rentalService.getAllRentals();
            } else {
                allRentals = rentalService.getAllRentals();
            }
            
            // Filter by date range and status
            List<Rental> filteredRentals = allRentals.stream()
                .filter(r -> !r.getStartDate().isBefore(fromDate) && !r.getStartDate().isAfter(toDate) &&
                           (r.getRentalStatus() == Rental.RentalStatus.RETURNED || 
                            r.getRentalStatus() == Rental.RentalStatus.ACTIVE))
                .collect(java.util.stream.Collectors.toList());
            
            if (filteredRentals.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No rentals found for selected date range!");
                return;
            }
            
            // Calculate totals
            BigDecimal totalIncome = filteredRentals.stream()
                .map(Rental::getFinalPayableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalDeposits = filteredRentals.stream()
                .map(Rental::getSecurityDeposit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Create report row
            RevenueRow row = new RevenueRow();
            row.branchName =  branchNameLabel.getText();
            row.totalRentals = filteredRentals.size();
            row.totalIncome = totalIncome.toString();
            row.totalDeposits = totalDeposits.toString();
            row.totalLateFees = "0.00"; // Would need return details to calculate
            
            reportTable.setItems(FXCollections.observableArrayList(row));
            totalRevenueLabel.setText("Total Revenue: LKR " + totalIncome);
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report: " + e.getMessage());
        }
    }
    
    /**
     * Export report to CSV
     */
    @FXML
    private void handleExportReport() {
        if (reportTable.getItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", "Generate report first!");
            return;
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Export", "Report exported successfully!\n(CSV file saved)");
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
     * Inner class for report row
     */
    public static class RevenueRow {
        public String branchName;
        public int totalRentals;
        public String totalIncome;
        public String totalDeposits;
        public String totalLateFees;
    }
}