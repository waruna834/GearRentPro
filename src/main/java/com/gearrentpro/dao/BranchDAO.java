package com.gearrentpro.dao;

import com.gearrentpro.entity.Branch;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {
    
    /**
     * Get all branches
     */
    public List<Branch> getAllBranches() throws SQLException {
        String sql = "SELECT * FROM branches ORDER BY branch_name";
        List<Branch> branches = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }
        }
        return branches;
    }
    
    /**
     * Get branch by ID
     */
    public Branch getBranchById(int branchId) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, branchId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get branch by code
     */
    public Branch getBranchByCode(String branchCode) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, branchCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new branch
     */
    public int createBranch(Branch branch) throws SQLException {
        String sql = "INSERT INTO branches (branch_code, branch_name, address, contact_number, email) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, branch.getBranchCode());
            stmt.setString(2, branch.getBranchName());
            stmt.setString(3, branch.getAddress());
            stmt.setString(4, branch.getContactNumber());
            stmt.setString(5, branch.getEmail());
            
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
     * Update branch
     */
    public boolean updateBranch(Branch branch) throws SQLException {
        String sql = "UPDATE branches SET branch_code = ?, branch_name = ?, address = ?, " +
                     "contact_number = ?, email = ? WHERE branch_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, branch.getBranchCode());
            stmt.setString(2, branch.getBranchName());
            stmt.setString(3, branch.getAddress());
            stmt.setString(4, branch.getContactNumber());
            stmt.setString(5, branch.getEmail());
            stmt.setInt(6, branch.getBranchId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Branch object
     */
    private Branch mapResultSetToBranch(ResultSet rs) throws SQLException {
        Branch branch = new Branch();
        branch.setBranchId(rs.getInt("branch_id"));
        branch.setBranchCode(rs.getString("branch_code"));
        branch.setBranchName(rs.getString("branch_name"));
        branch.setAddress(rs.getString("address"));
        branch.setContactNumber(rs.getString("contact_number"));
        branch.setEmail(rs.getString("email"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            branch.setCreatedAt(ts.toLocalDateTime());
        }
        
        return branch;
    }
}