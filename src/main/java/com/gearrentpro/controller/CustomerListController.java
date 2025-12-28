package com.gearrentpro.controller;

import com.gearrentpro.entity.Customer;
import com.gearrentpro.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class CustomerListController {
    
    @FXML
    private TableView<Customer> customerTable;
    
    @FXML
    private TableColumn<Customer, String> codeColumn;
    
    @FXML
    private TableColumn<Customer, String> nameColumn;
    
    @FXML
    private TableColumn<Customer, String> nicColumn;
    
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    
    @FXML
    private TableColumn<Customer, String> membershipColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private HBox actionButtonsBox;
    
    private CustomerService customerService;
    private List<Customer> allCustomers;
    
    @FXML
    public void initialize() {
        customerService = CustomerService.getInstance();
        setupTableColumns();
        loadCustomers();
        
    }
    
    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerCode()));
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));
        nicColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNicPassport()));
        phoneColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactNumber()));
        membershipColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMembershipLevel().toString()));
    }
    
    private void loadCustomers() {
        try {
            allCustomers = customerService.getAllCustomers();
            ObservableList<Customer> observableList = FXCollections.observableArrayList(allCustomers);
            customerTable.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadCustomers();
            return;
        }
        
        List<Customer> filtered = allCustomers.stream()
            .filter(c -> c.getCustomerName().toLowerCase().contains(searchText) ||
                        c.getCustomerCode().toLowerCase().contains(searchText) ||
                        c.getContactNumber().contains(searchText))
            .collect(java.util.stream.Collectors.toList());
        
        ObservableList<Customer> observableList = FXCollections.observableArrayList(filtered);
        customerTable.setItems(observableList);
    }
    
    @FXML
    private void handleAddCustomer() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        dialog.setHeaderText("Create new customer");
        
        TextField codeField = new TextField();
        codeField.setPromptText("Customer Code");
        TextField nameField = new TextField();
        nameField.setPromptText("Customer Name");
        TextField nicField = new TextField();
        nicField.setPromptText("NIC/Passport");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        ComboBox<Customer.MembershipLevel> membershipCombo = new ComboBox<>();
        membershipCombo.setItems(FXCollections.observableArrayList(Customer.MembershipLevel.values()));
        membershipCombo.setValue(Customer.MembershipLevel.REGULAR);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Customer Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("NIC/Passport:"), 0, 2);
        grid.add(nicField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Membership:"), 0, 6);
        grid.add(membershipCombo, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                Customer customer = new Customer(codeField.getText(), nameField.getText(), 
                                                nicField.getText(), phoneField.getText(), 
                                                emailField.getText(), addressField.getText());
                customer.setMembershipLevel(membershipCombo.getValue());
                return customer;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(customer -> {
            try {
                if (customerService.createCustomer(customer)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
                    loadCustomers();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add customer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleUpdateCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a customer to update!");
            return;
        }
        
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Update Customer");
        dialog.setHeaderText("Edit customer information");
        
        TextField codeField = new TextField();
        codeField.setText(selected.getCustomerCode());
        codeField.setDisable(true);
        
        TextField nameField = new TextField();
        nameField.setText(selected.getCustomerName());
        TextField nicField = new TextField();
        nicField.setText(selected.getNicPassport());
        nicField.setDisable(true);
        TextField phoneField = new TextField();
        phoneField.setText(selected.getContactNumber());
        TextField emailField = new TextField();
        emailField.setText(selected.getEmail());
        TextField addressField = new TextField();
        addressField.setText(selected.getAddress());
        
        ComboBox<Customer.MembershipLevel> membershipCombo = new ComboBox<>();
        membershipCombo.setItems(FXCollections.observableArrayList(Customer.MembershipLevel.values()));
        membershipCombo.setValue(selected.getMembershipLevel());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Customer Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("NIC/Passport:"), 0, 2);
        grid.add(nicField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Membership:"), 0, 6);
        grid.add(membershipCombo, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == updateButton) {
                selected.setCustomerName(nameField.getText());
                selected.setContactNumber(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setAddress(addressField.getText());
                selected.setMembershipLevel(membershipCombo.getValue());
                return selected;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(customer -> {
            try {
                if (customerService.updateCustomer(customer)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
                    loadCustomers();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update customer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleDeleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a customer to delete!");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Customer");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Delete customer: " + selected.getCustomerName() + "\n\nThis action cannot be undone!");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully!");
                loadCustomers();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete customer: " + e.getMessage());
            }
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}