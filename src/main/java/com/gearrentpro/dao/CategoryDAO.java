package com.gearrentpro.dao;

import com.gearrentpro.entity.Category;
import com.gearrentpro.util.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryDAO {
    
    /**
     * Get all active categories
     */
    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM categories WHERE status = 'ACTIVE' ORDER BY category_name";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        return categories;
    }
    
    /**
     * Get category by ID
     */
    public Category getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get category by name
     */
    public Category getCategoryByName(String categoryName) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_name = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new category
     */
    public int createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description, base_price_factor, " +
                     "weekend_multiplier, default_late_fee, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setBigDecimal(3, category.getBasePriceFactor());
            stmt.setBigDecimal(4, category.getWeekendMultiplier());
            stmt.setBigDecimal(5, category.getDefaultLateFee());
            stmt.setString(6, category.getStatus().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
    
    /**
     * Update category
     */
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, description = ?, base_price_factor = ?, " +
                     "weekend_multiplier = ?, default_late_fee = ?, status = ? WHERE category_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setBigDecimal(3, category.getBasePriceFactor());
            stmt.setBigDecimal(4, category.getWeekendMultiplier());
            stmt.setBigDecimal(5, category.getDefaultLateFee());
            stmt.setString(6, category.getStatus().toString());
            stmt.setInt(7, category.getCategoryId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Category object
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setBasePriceFactor(rs.getBigDecimal("base_price_factor"));
        category.setWeekendMultiplier(rs.getBigDecimal("weekend_multiplier"));
        category.setDefaultLateFee(rs.getBigDecimal("default_late_fee"));
        category.setStatus(Category.CategoryStatus.valueOf(rs.getString("status")));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            category.setCreatedAt(ts.toLocalDateTime());
        }
        
        return category;
    }
}