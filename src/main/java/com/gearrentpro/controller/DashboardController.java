package com.gearrentpro.controller;

import com.gearrentpro.entity.User;
import com.gearrentpro.service.AuthenticationService;
import com.gearrentpro.dao.BranchDAO;
import com.gearrentpro.entity.Branch;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label userLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label branchLabel;

    @FXML
    private VBox menuVBox;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();
        User currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            userLabel.setText("User: " + currentUser.getFullName());
            roleLabel.setText("Role: " + currentUser.getRole().toString());
            // Show branch name if user is not ADMIN and has a branchId
            if (currentUser.getRole() != User.UserRole.ADMIN && currentUser.getBranchId() != null) {
                try {
                    BranchDAO branchDAO = new BranchDAO();
                    Branch branch = branchDAO.getBranchById(currentUser.getBranchId());
                    if (branch != null) {
                        branchLabel.setText("Branch: " + branch.getBranchName());
                    } else {
                        branchLabel.setText("Branch: N/A");
                    }
                } catch (Exception e) {
                    branchLabel.setText("Branch: Error");
                }
            } else {
                branchLabel.setText("");
            }
        } else {
            branchLabel.setText("");
        }

        // Call setupMenu AFTER authService is set
        setupMenu();
    }

    /**
     * Setup menu based on user role
     */
    private void setupMenu() {
        menuVBox.getChildren().clear();

        // ===== MAIN DASHBOARD =====
        // addMenuItem("Dashboard Home", "Dashboard");
        // addSeparator();

        // ===== CORE OPERATIONS =====
        if (authService.isBranchManager() || authService.isStaff()) {
            Label operationsLabel = new Label("CORE OPERATIONS");
            operationsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #131f2cff; -fx-padding: 10 0 5 0;");
            menuVBox.getChildren().add(operationsLabel);

            addMenuItem("Customers", "CustomerList");
            addMenuItem("Equipment", "EquipmentList");
            addMenuItem("Create Rental", "CreateRental");
            addMenuItem("Reservations", "ReservationList");
            addMenuItem("Rentals", "RentalList");
            addMenuItem("Overdue Rentals", "OverdueRentals");
            addMenuItem("Process Return", "ProcessReturn");

            addSeparator();
        }

        // ===== ADMIN ONLY SECTION =====
        if (authService.isAdmin()) {
            Label adminLabel = new Label("ADMIN FUNCTIONS");
            adminLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-padding: 10 0 5 0;");
            menuVBox.getChildren().add(adminLabel);

            addMenuItem("Manage Branches", "ManageBranches");
            addMenuItem("Manage Categories", "ManageCategories");
            addMenuItem("Manage Users", "ManageUsers");
            addMenuItem("Manage Equipment", "ManageEquipment");
            addMenuItem("Configuration", "Configuration");

            addSeparator();
        }

        // ===== REPORTS SECTION =====
        if (authService.isBranchManager()) {
            Label reportsLabel = new Label("REPORTS");
            reportsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-padding: 10 0 5 0;");
            menuVBox.getChildren().add(reportsLabel);

            addMenuItem("Revenue Report", "RevenueReport");
            addMenuItem("Equipment Utilization", "EquipmentUtilization");

            addSeparator();
        }

        // ===== ACCOUNT SECTION =====
        Label accountLabel = new Label("ACCOUNT");
        accountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8e44ad; -fx-padding: 10 0 5 0;");
        menuVBox.getChildren().add(accountLabel);

        addMenuItem("Logout", null);
    }

    /**
     * Add menu item to VBox
     */
    private void addMenuItem(String text, String fxmlFile) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 12 15;" +
                        "-fx-text-alignment: left;" +
                        "-fx-background-color: #ecf0f1;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-cursor: hand;");

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 12 15;" +
                        "-fx-text-alignment: left;" +
                        "-fx-background-color: #d5dbdb;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-cursor: hand;"));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-padding: 12 15;" +
                        "-fx-text-alignment: left;" +
                        "-fx-background-color: #ecf0f1;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-cursor: hand;"));

        btn.setOnAction(e -> {
            if (fxmlFile == null) {
                handleLogout();
            } else {
                loadScreen(fxmlFile);
            }
        });

        menuVBox.getChildren().add(btn);
    }

    /**
     * Add separator to menu
     */
    private void addSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-padding: 5 0 5 0;");
        menuVBox.getChildren().add(sep);
    }

    /**
     * Load screen in center pane
     * Handles both VBox and ScrollPane root elements
     */
    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile + ".fxml"));
            Object root = loader.load();

            // Handle both VBox and ScrollPane as root elements
            if (root instanceof VBox) {
                VBox content = (VBox) root;
                mainBorderPane.setCenter(content);
            } else if (root instanceof ScrollPane) {
                ScrollPane content = (ScrollPane) root;
                mainBorderPane.setCenter(content);
            } else {
                // Fallback for other types
                mainBorderPane.setCenter((javafx.scene.Node) root);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle logout
     */
    private void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Logout");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("You are about to logout from the system.");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            authService.logout();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                BorderPane login = loader.load();

                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                stage.setScene(new Scene(login, 500, 600));
                stage.setTitle("GearRent Pro - Login");
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to login screen!");
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