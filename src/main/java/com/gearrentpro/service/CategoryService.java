package com.gearrentpro.service;

import com.gearrentpro.dao.CategoryDAO;
import com.gearrentpro.entity.Category;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private CategoryDAO categoryDAO;
    private static CategoryService instance;
    
    private CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }
    
    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }
    
    /**
     * Get all active categories
     */
    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }
    
    /**
     * Get category by ID
     */
    public Category getCategoryById(int categoryId) throws SQLException {
        return categoryDAO.getCategoryById(categoryId);
    }
    
    /**
     * Create new category with validation
     */
    public boolean createCategory(Category category) throws SQLException {
        // Validate
        validateCategory(category);
        
        // Check if category name already exists
        if (categoryDAO.getCategoryByName(category.getCategoryName()) != null) {
            throw new IllegalArgumentException("Category name already exists!");
        }
        
        int categoryId = categoryDAO.createCategory(category);
        return categoryId > 0;
    }
    
    /**
     * Update category
     */
    public boolean updateCategory(Category category) throws SQLException {
        validateCategory(category);
        return categoryDAO.updateCategory(category);
    }
    
    /**
     * Validate category data
     */
    private void validateCategory(Category category) throws IllegalArgumentException {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required!");
        }
        
        if (category.getBasePriceFactor() == null || category.getBasePriceFactor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price factor must be greater than 0!");
        }
        
        if (category.getWeekendMultiplier() == null || category.getWeekendMultiplier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weekend multiplier must be greater than 0!");
        }
    }
}