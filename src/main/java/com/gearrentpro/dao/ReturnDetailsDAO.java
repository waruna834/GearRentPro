package com.gearrentpro.dao;

import com.gearrentpro.entity.ReturnDetails;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;

public class ReturnDetailsDAO {
    
    /**
     * Get return details by rental ID
     */
    public ReturnDetails getReturnDetailsByRentalId(int rentalId) throws SQLException {
        String sql = "SELECT * FROM return_details WHERE rental_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rentalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReturnDetails(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create return details record
     */
    public int createReturnDetails(ReturnDetails returnDetails) throws SQLException {
        String sql = "INSERT INTO return_details (rental_id, damage_description, damage_charge, " +
                     "late_fee, total_charges, refund_amount, additional_payment_required) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, returnDetails.getRentalId());
            stmt.setString(2, returnDetails.getDamageDescription());
            stmt.setBigDecimal(3, returnDetails.getDamageCharge());
            stmt.setBigDecimal(4, returnDetails.getLateFee());
            stmt.setBigDecimal(5, returnDetails.getTotalCharges());
            stmt.setBigDecimal(6, returnDetails.getRefundAmount());
            stmt.setBigDecimal(7, returnDetails.getAdditionalPaymentRequired());
            
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
     * Update return details
     */
    public boolean updateReturnDetails(ReturnDetails returnDetails) throws SQLException {
        String sql = "UPDATE return_details SET damage_description = ?, damage_charge = ?, " +
                     "late_fee = ?, total_charges = ?, refund_amount = ?, " +
                     "additional_payment_required = ? WHERE rental_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, returnDetails.getDamageDescription());
            stmt.setBigDecimal(2, returnDetails.getDamageCharge());
            stmt.setBigDecimal(3, returnDetails.getLateFee());
            stmt.setBigDecimal(4, returnDetails.getTotalCharges());
            stmt.setBigDecimal(5, returnDetails.getRefundAmount());
            stmt.setBigDecimal(6, returnDetails.getAdditionalPaymentRequired());
            stmt.setInt(7, returnDetails.getRentalId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to ReturnDetails object
     */
    private ReturnDetails mapResultSetToReturnDetails(ResultSet rs) throws SQLException {
        ReturnDetails returnDetails = new ReturnDetails();
        returnDetails.setReturnId(rs.getInt("return_id"));
        returnDetails.setRentalId(rs.getInt("rental_id"));
        returnDetails.setDamageDescription(rs.getString("damage_description"));
        returnDetails.setDamageCharge(rs.getBigDecimal("damage_charge"));
        returnDetails.setLateFee(rs.getBigDecimal("late_fee"));
        returnDetails.setTotalCharges(rs.getBigDecimal("total_charges"));
        returnDetails.setRefundAmount(rs.getBigDecimal("refund_amount"));
        returnDetails.setAdditionalPaymentRequired(rs.getBigDecimal("additional_payment_required"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            returnDetails.setCreatedAt(ts.toLocalDateTime());
        }
        
        return returnDetails;
    }
}