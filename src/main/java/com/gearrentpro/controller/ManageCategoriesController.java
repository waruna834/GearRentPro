package com.gearrentpro.controller;

import com.gearrentpro.entity.Category;
import com.gearrentpro.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ManageCategoriesController {
    
    @FXML
    private TableView<Category> categoryTable;
    
    @FXML
    private TableColumn<Category, String> nameColumn;
    
    @FXML
    private TableColumn<Category, String> descriptionColumn;
    
    @FXML
    private TableColumn<Category, String> factorColumn;
    
    @FXML
    private TableColumn<Category, String> multiplierColumn;
    
    @FXML
    private TableColumn<Category, String> lateFeeColumn;
    
    @FXML
    private TableColumn<Category, String> statusColumn;
    
    private CategoryService categoryService;
    
    @FXML
    public void initialize() {
        categoryService = CategoryService.getInstance();
        setupTableColumns();
        loadCategories();
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoryName()));
        descriptionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        factorColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBasePriceFactor().toString()));
        multiplierColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getWeekendMultiplier().toString()));
        lateFeeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("LKR " + cellData.getValue().getDefaultLateFee()));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }
    
    /**
     * Load all categories
     */
    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            categoryTable.setItems(FXCollections.observableArrayList(categories));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load categories: " + e.getMessage());
        }
    }
    
    /**
     * Handle add category button
     */
    @FXML
    private void handleAddCategory() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Create a new equipment category");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Category Name (e.g., Camera)");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField factorField = new TextField();
        factorField.setPromptText("Base Price Factor (e.g., 1.5)");
        TextField multiplierField = new TextField();
        multiplierField.setPromptText("Weekend Multiplier (e.g., 1.2)");
        TextField feeField = new TextField();
        feeField.setPromptText("Late Fee Per Day (e.g., 500)");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Base Price Factor:"), 0, 2);
        grid.add(factorField, 1, 2);
        grid.add(new Label("Weekend Multiplier:"), 0, 3);
        grid.add(multiplierField, 1, 3);
        grid.add(new Label("Late Fee Per Day:"), 0, 4);
        grid.add(feeField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    Category cat = new Category();
                    cat.setCategoryName(nameField.getText());
                    cat.setDescription(descField.getText());
                    cat.setBasePriceFactor(new BigDecimal(factorField.getText()));
                    cat.setWeekendMultiplier(new BigDecimal(multiplierField.getText()));
                    cat.setDefaultLateFee(new BigDecimal(feeField.getText()));
                    cat.setStatus(Category.CategoryStatus.ACTIVE);
                    return cat;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers!");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(category -> {
            try {
                if (categoryService.createCategory(category)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully!");
                    loadCategories();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add category: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    /**
     * Handle update category button
     */
    @FXML
    private void handleUpdateCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a category to update!");
            return;
        }
        
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Update Category");
        dialog.setHeaderText("Edit category information");
        
        TextField nameField = new TextField();
        nameField.setText(selected.getCategoryName());
        nameField.setDisable(true);
        TextField descField = new TextField();
        descField.setText(selected.getDescription());
        TextField factorField = new TextField();
        factorField.setText(selected.getBasePriceFactor().toString());
        TextField multiplierField = new TextField();
        multiplierField.setText(selected.getWeekendMultiplier().toString());
        TextField feeField = new TextField();
        feeField.setText(selected.getDefaultLateFee().toString());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefWidth(400);
        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Base Price Factor:"), 0, 2);
        grid.add(factorField, 1, 2);
        grid.add(new Label("Weekend Multiplier:"), 0, 3);
        grid.add(multiplierField, 1, 3);
        grid.add(new Label("Late Fee Per Day:"), 0, 4);
        grid.add(feeField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == updateButton) {
                try {
                    selected.setDescription(descField.getText());
                    selected.setBasePriceFactor(new BigDecimal(factorField.getText()));
                    selected.setWeekendMultiplier(new BigDecimal(multiplierField.getText()));
                    selected.setDefaultLateFee(new BigDecimal(feeField.getText()));
                    return selected;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter valid numbers!");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(category -> {
            try {
                if (categoryService.updateCategory(category)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully!");
                    loadCategories();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update category: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        });
    }
    
    /**
     * Handle delete category button
     */
    @FXML
    private void handleDeleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a category to delete!");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Category");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will delete the category: " + selected.getCategoryName() + 
                                    "\n\nThis action cannot be undone!");
        
        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully!");
                loadCategories();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete category: " + e.getMessage());
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