package com.gearrentpro.dao;

import com.gearrentpro.entity.Equipment;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    
    /**
     * Get all equipment
     */
    public List<Equipment> getAllEquipment() throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN branches b ON e.branch_id = b.branch_id ORDER BY e.equipment_code";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        }
        return equipmentList;
    }
    
    /**
     * Get equipment by ID
     */
    public Equipment getEquipmentById(int equipmentId) throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN branches b ON e.branch_id = b.branch_id WHERE e.equipment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, equipmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEquipment(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get equipment by branch ID
     */
    public List<Equipment> getEquipmentByBranch(int branchId) throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.branch_id = ? ORDER BY e.equipment_code";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, branchId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipmentList.add(mapResultSetToEquipment(rs));
                }
            }
        }
        return equipmentList;
    }
    
    /**
     * Get equipment by category and branch
     */
    public List<Equipment> getEquipmentByBranchAndCategory(int branchId, int categoryId) throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.branch_id = ? AND e.category_id = ? ORDER BY e.equipment_code";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, branchId);
            stmt.setInt(2, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipmentList.add(mapResultSetToEquipment(rs));
                }
            }
        }
        return equipmentList;
    }
    
    /**
     * Get available equipment by branch and category
     */
    public List<Equipment> getAvailableEquipment(int branchId, int categoryId) throws SQLException {
        String sql = "SELECT e.*, c.category_name, b.branch_name FROM equipment e " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.branch_id = ? AND e.category_id = ? AND e.status = 'AVAILABLE' " +
                     "ORDER BY e.equipment_code";
        List<Equipment> equipmentList = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, branchId);
            stmt.setInt(2, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipmentList.add(mapResultSetToEquipment(rs));
                }
            }
        }
        return equipmentList;
    }

    public boolean isEquipmentAvailable(int equipmentId, java.time.LocalDate startDate, java.time.LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rentals WHERE equipment_id = ? AND rental_status = 'ACTIVE' AND " +
                     "((start_date <= ? AND end_date >= ?) OR " +
                     "(start_date <= ? AND end_date >= ?) OR " +
                     "(start_date >= ? AND end_date <= ?))";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipmentId);
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));
            stmt.setDate(5, java.sql.Date.valueOf(startDate));
            stmt.setDate(6, java.sql.Date.valueOf(startDate));
            stmt.setDate(7, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return false; // The equipment is rented
                    }
                }
            }
        }

        // Now check reservations
        String reservationSql = "SELECT COUNT(*) FROM reservations WHERE equipment_id = ? AND status = 'CONFIRMED' AND " +
                                "((start_date <= ? AND end_date >= ?) OR " +
                                "(start_date <= ? AND end_date >= ?) OR " +
                                "(start_date >= ? AND end_date <= ?))";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(reservationSql)) {

            stmt.setInt(1, equipmentId);
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));
            stmt.setDate(5, java.sql.Date.valueOf(startDate));
            stmt.setDate(6, java.sql.Date.valueOf(startDate));
            stmt.setDate(7, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return false; // The equipment is reserved
                    }
                }
            }
        }

        return true; // The equipment is available
    }
    
    /**
     * Create new equipment
     */
    public int createEquipment(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment (equipment_code, category_id, brand, model, purchase_year, " +
                     "daily_base_price, security_deposit, status, branch_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipment.getEquipmentCode());
            stmt.setInt(2, equipment.getCategoryId());
            stmt.setString(3, equipment.getBrand());
            stmt.setString(4, equipment.getModel());
            stmt.setInt(5, equipment.getPurchaseYear());
            stmt.setBigDecimal(6, equipment.getDailyBasePrice());
            stmt.setBigDecimal(7, equipment.getSecurityDeposit());
            stmt.setString(8, equipment.getStatus().toString());
            stmt.setInt(9, equipment.getBranchId());
            
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
     * Update equipment
     */
    public boolean updateEquipment(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipment SET equipment_code = ?, category_id = ?, brand = ?, model = ?, " +
                     "purchase_year = ?, daily_base_price = ?, security_deposit = ?, status = ?, " +
                     "branch_id = ? WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipment.getEquipmentCode());
            stmt.setInt(2, equipment.getCategoryId());
            stmt.setString(3, equipment.getBrand());
            stmt.setString(4, equipment.getModel());
            stmt.setInt(5, equipment.getPurchaseYear());
            stmt.setBigDecimal(6, equipment.getDailyBasePrice());
            stmt.setBigDecimal(7, equipment.getSecurityDeposit());
            stmt.setString(8, equipment.getStatus().toString());
            stmt.setInt(9, equipment.getBranchId());
            stmt.setInt(10, equipment.getEquipmentId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update equipment status
     */
    public boolean updateEquipmentStatus(int equipmentId, Equipment.EquipmentStatus status) throws SQLException {
        String sql = "UPDATE equipment SET status = ? WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setInt(2, equipmentId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Equipment object
     */
    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(rs.getInt("equipment_id"));
        equipment.setEquipmentCode(rs.getString("equipment_code"));
        equipment.setCategoryId(rs.getInt("category_id"));
        equipment.setCategoryName(rs.getString("category_name"));
        equipment.setBrand(rs.getString("brand"));
        equipment.setModel(rs.getString("model"));
        equipment.setPurchaseYear(rs.getInt("purchase_year"));
        equipment.setDailyBasePrice(rs.getBigDecimal("daily_base_price"));
        equipment.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        equipment.setStatus(Equipment.EquipmentStatus.valueOf(rs.getString("status")));
        equipment.setBranchId(rs.getInt("branch_id"));
        equipment.setBranchName(rs.getString("branch_name"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            equipment.setCreatedAt(ts.toLocalDateTime());
        }
        
        return equipment;
    }
}