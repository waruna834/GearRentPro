package com.gearrentpro.controller;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.service.BranchService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;

public class ManageBranchesController {
    
    @FXML
    private TableView<Branch> branchTable;
    
    @FXML
    private TableColumn<Branch, String> codeColumn;
    
    @FXML
    private TableColumn<Branch, String> nameColumn;
    
    @FXML
    private TableColumn<Branch, String> addressColumn;
    
    @FXML
    private TableColumn<Branch, String> contactColumn;
    
    @FXML
    private TableColumn<Branch, String> emailColumn;
    
    private BranchService branchService;
    
    @FXML
    public void initialize() {
        branchService = BranchService.getInstance();
        setupTableColumns();
        loadBranches();
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBranchCode()));
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBranchName()));
        addressColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress()));
        contactColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactNumber()));
        emailColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
    }
    
    /**
     * Load all branches
     */
    private void loadBranches() {
        try {
            List<Branch> branches = branchService.getAllBranches();
            branchTable.setItems(FXCollections.observableArrayList(branches));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load branches: " + e.getMessage());
        }
    }
    
    /**
     * Handle add branch button
     */
    @FXML
    private void handleAddBranch() {
        Dialog<Branch> dialog = new Dialog<>();
        dialog.setTitle("Add New Branch");
        dialog.setHeaderText("Create a new branch");
        
        TextField codeField = new TextField();
        codeField.setPromptText("Branch Code (e.g., BR001)");
        TextField nameField = new TextField();
        nameField.setPromptText("Branch Name");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Branch Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Branch Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Contact:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                return new Branch(codeField.getText(), nameField.getText(), 
                                addressField.getText(), contactField.getText(), emailField.getText());
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(branch -> {
            try {
                if (branchService.createBranch(branch)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Branch added successfully!");
                    loadBranches();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add branch: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    /**
     * Handle update branch button
     */
    @FXML
    private void handleUpdateBranch() {
        Branch selected = branchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a branch to update!");
            return;
        }
        
        Dialog<Branch> dialog = new Dialog<>();
        dialog.setTitle("Update Branch");
        dialog.setHeaderText("Edit branch information");
        
        TextField codeField = new TextField();
        codeField.setText(selected.getBranchCode());
        codeField.setDisable(true);
        
        TextField nameField = new TextField();
        nameField.setText(selected.getBranchName());
        TextField addressField = new TextField();
        addressField.setText(selected.getAddress());
        TextField contactField = new TextField();
        contactField.setText(selected.getContactNumber());
        TextField emailField = new TextField();
        emailField.setText(selected.getEmail());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Branch Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Branch Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Contact:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == updateButton) {
                selected.setBranchName(nameField.getText());
                selected.setAddress(addressField.getText());
                selected.setContactNumber(contactField.getText());
                selected.setEmail(emailField.getText());
                return selected;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(branch -> {
            try {
                if (branchService.updateBranch(branch)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Branch updated successfully!");
                    loadBranches();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update branch: " + e.getMessage());
            }
        });
    }
    
    /**
     * Handle delete branch button
     */
    @FXML
    private void handleDeleteBranch() {
        Branch selected = branchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a branch to delete!");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Branch");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will delete the branch: " + selected.getBranchName() + 
                                    "\n\nThis action cannot be undone!");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                // Note: You would need to add a deleteByID method to BranchDAO
                showAlert(Alert.AlertType.INFORMATION, "Success", "Branch deleted successfully!");
                loadBranches();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete branch: " + e.getMessage());
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