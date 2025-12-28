package com.gearrentpro.dao;

import com.gearrentpro.entity.Reservation;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    
    /**
     * Get all active reservations
     */
    public List<Reservation> getAllReservations() throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM reservations r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.status IN ('PENDING', 'CONFIRMED', 'CANCELLED') ORDER BY r.start_date";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        return reservations;
    }
    
    /**
     * Get reservations by branch
     */
    public List<Reservation> getReservationsByBranch(int branchId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM reservations r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.branch_id = ? AND r.status IN ('PENDING', 'CONFIRMED') ORDER BY r.start_date";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        return reservations;
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(int reservationId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM reservations r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.reservation_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get reservations by equipment
     */
    public List<Reservation> getReservationsByEquipment(int equipmentId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM reservations r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.equipment_id = ? AND r.status IN ('PENDING', 'CONFIRMED') " +
                     "ORDER BY r.start_date";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, equipmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        return reservations;
    }
    
    /**
     * Check if equipment has overlapping reservations in date range
     */
    public boolean hasOverlappingReservation(int equipmentId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM reservations " +
                     "WHERE equipment_id = ? AND status IN ('PENDING', 'CONFIRMED') " +
                     "AND ((start_date <= ? AND end_date >= ?) OR " +
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
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Create new reservation
     */
    public int createReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (reservation_code, equipment_id, customer_id, " +
                     "branch_id, start_date, end_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, reservation.getReservationCode());
            stmt.setInt(2, reservation.getEquipmentId());
            stmt.setInt(3, reservation.getCustomerId());
            stmt.setInt(4, reservation.getBranchId());
            stmt.setDate(5, java.sql.Date.valueOf(reservation.getStartDate()));
            stmt.setDate(6, java.sql.Date.valueOf(reservation.getEndDate()));
            stmt.setString(7, reservation.getStatus().toString());
            
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
     * Update reservation status
     */
    public boolean updateReservationStatus(int reservationId, Reservation.ReservationStatus status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setInt(2, reservationId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Reservation object
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setReservationCode(rs.getString("reservation_code"));
        reservation.setEquipmentId(rs.getInt("equipment_id"));
        reservation.setCustomerId(rs.getInt("customer_id"));
        reservation.setBranchId(rs.getInt("branch_id"));
        reservation.setStartDate(rs.getDate("start_date").toLocalDate());
        reservation.setEndDate(rs.getDate("end_date").toLocalDate());
        reservation.setStatus(Reservation.ReservationStatus.valueOf(rs.getString("status")));
        
        // Join fields
        reservation.setEquipmentDetails(rs.getString("brand") + " " + rs.getString("model"));
        reservation.setCustomerName(rs.getString("customer_name"));
        reservation.setBranchName(rs.getString("branch_name"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            reservation.setCreatedAt(ts.toLocalDateTime());
        }
        
        return reservation;
    }
}