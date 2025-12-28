package com.gearrentpro.controller;

import com.gearrentpro.entity.User;
import com.gearrentpro.entity.Branch;
import com.gearrentpro.service.BranchService;
import com.gearrentpro.dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;

public class ManageUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> branchColumn;

    @FXML
    private TableColumn<User, String> statusColumn;

    private UserDAO userDAO;
    private BranchService branchService;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        branchService = BranchService.getInstance();
        setupTableColumns();
        loadUsers();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole().toString()));
        branchColumn.setCellValueFactory(cellData -> {
            Integer branchId = cellData.getValue().getBranchId();
            if (branchId != null) {
                try {
                    Branch branch = branchService.getBranchById(branchId);
                    return new javafx.beans.property.SimpleStringProperty(
                            branch != null ? branch.getBranchName() : "N/A");
                } catch (SQLException e) {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        statusColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }

    /**
     * Load all users from database
     */
    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));

            if (users.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "No users found. Click 'Add New User' to create users.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load users: " + e.getMessage());
        }
    }

    /**
     * Handle add user button
     */
    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new system user");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        ComboBox<User.UserRole> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(User.UserRole.values()));
        roleCombo.setValue(User.UserRole.STAFF);

        ComboBox<Branch> branchCombo = new ComboBox<>();
        try {
            List<Branch> branches = branchService.getAllBranches();
            branchCombo.setItems(FXCollections.observableArrayList(branches));
            if (!branches.isEmpty()) {
                branchCombo.setValue(branches.get(0));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load branches!");
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);
        grid.add(new Label("Branch:"), 0, 5);
        grid.add(branchCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setPassword(passwordField.getText());
                user.setFullName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setRole(roleCombo.getValue());
                user.setStatus(User.UserStatus.ACTIVE);

                Branch selectedBranch = branchCombo.getValue();
                if (selectedBranch != null && roleCombo.getValue() != User.UserRole.ADMIN) {
                    user.setBranchId(selectedBranch.getBranchId());
                }

                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            try {
                int userId = userDAO.createUser(user);
                if (userId > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user: " + e.getMessage());
            }
        });
    }

    /**
     * Handle update user button - COMPLETE IMPLEMENTATION
     */
    @FXML
    private void handleUpdateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a user to update!");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Update User");
        dialog.setHeaderText("Edit user information");

        TextField usernameField = new TextField();
        usernameField.setText(selected.getUsername());
        usernameField.setDisable(true); // Username cannot be changed

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");

        TextField nameField = new TextField();
        nameField.setText(selected.getFullName());

        TextField emailField = new TextField();
        emailField.setText(selected.getEmail());

        ComboBox<User.UserRole> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(User.UserRole.values()));
        roleCombo.setValue(selected.getRole());

        ComboBox<Branch> branchCombo = new ComboBox<>();
        try {
            List<Branch> branches = branchService.getAllBranches();
            branchCombo.setItems(FXCollections.observableArrayList(branches));

            if (selected.getBranchId() != null) {
                Branch currentBranch = branchService.getBranchById(selected.getBranchId());
                if (currentBranch != null) {
                    branchCombo.setValue(currentBranch);
                }
            } else if (!branches.isEmpty()) {
                branchCombo.setValue(branches.get(0));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load branches!");
        }

        // Disable branch combo for ADMIN role
        if (selected.getRole() == User.UserRole.ADMIN) {
            branchCombo.setDisable(true);
        }

        roleCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == User.UserRole.ADMIN) {
                branchCombo.setDisable(true);
            } else {
                branchCombo.setDisable(false);
            }
        });

        ComboBox<User.UserStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(User.UserStatus.values()));
        statusCombo.setValue(selected.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);
        grid.add(new Label("Branch:"), 0, 5);
        grid.add(branchCombo, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == updateButton) {
                // Validate inputs
                if (nameField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Full Name cannot be empty!");
                    return null;
                }

                if (emailField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Email cannot be empty!");
                    return null;
                }

                // Update user object
                selected.setFullName(nameField.getText());
                selected.setEmail(emailField.getText());
                selected.setRole(roleCombo.getValue());
                selected.setStatus(statusCombo.getValue());

                // Only update password if provided
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }

                // Set branch only for non-admin users
                if (roleCombo.getValue() != User.UserRole.ADMIN) {
                    Branch selectedBranch = branchCombo.getValue();
                    if (selectedBranch != null) {
                        selected.setBranchId(selectedBranch.getBranchId());
                    }
                } else {
                    selected.setBranchId(null);
                }

                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            try {
                if (userDAO.updateUser(user)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update user: " + e.getMessage());
            }
        });
    }

    /**
     * Handle delete user button - COMPLETE IMPLEMENTATION
     */
    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a user to delete!");
            return;
        }

        // Prevent deleting admin user
        if (selected.getRole() == User.UserRole.ADMIN) {
            showAlert(Alert.AlertType.WARNING, "Cannot Delete", "Cannot delete admin user!");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete User");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will permanently delete the user: " + selected.getFullName() +
                "\nUsername: " + selected.getUsername() +
                "\n\nThis action cannot be undone!");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                // Update user status to INACTIVE instead of deleting
                selected.setStatus(User.UserStatus.INACTIVE);

                if (userDAO.updateUser(selected)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "User '" + selected.getFullName() + "' has been deactivated successfully!");
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete user: " + e.getMessage());
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