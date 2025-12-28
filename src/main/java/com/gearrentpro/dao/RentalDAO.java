package com.gearrentpro.dao;

import com.gearrentpro.entity.Rental;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {
    
    /**
     * Get all active rentals
     */
    public List<Rental> getAllRentals() throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "ORDER BY r.start_date DESC";
        List<Rental> rentals = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }

    /**
     * Get rentals by branch
     */
    public List<Rental> getRentalsByBranch(int branchId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.branch_id = ? ORDER BY r.start_date DESC";
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapResultSetToRental(rs));
                }
            }
        }
        return rentals;
    }
    
    /**
     * Get rental by ID
     */
    public Rental getRentalById(int rentalId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.rental_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rentalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRental(rs);
                }
            }
        }
        return null;
    }
    
    public Rental getRentalByReservationId(int reservationId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
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
                    return mapResultSetToRental(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get active rentals by customer
     */
    public List<Rental> getActiveRentalsByCustomer(int customerId) throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.customer_id = ? AND r.rental_status = 'ACTIVE' " +
                     "ORDER BY r.start_date";
        List<Rental> rentals = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapResultSetToRental(rs));
                }
            }
        }
        return rentals;
    }
    
    /**
     * Get overdue rentals
     */
    public List<Rental> getOverdueRentals() throws SQLException {
        String sql = "SELECT r.*, e.equipment_code, e.brand, e.model, c.category_name, " +
                     "cus.customer_name, b.branch_name FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "JOIN categories c ON e.category_id = c.category_id " +
                     "JOIN customers cus ON r.customer_id = cus.customer_id " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "WHERE r.rental_status = 'ACTIVE' AND CURDATE() > r.end_date " +
                     "ORDER BY r.end_date ASC";
        List<Rental> rentals = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }
    
    /**
     * Check if equipment is rented in date range
     */
    public boolean isEquipmentRented(int equipmentId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM rentals " +
                     "WHERE equipment_id = ? AND rental_status IN ('ACTIVE', 'OVERDUE') " +
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
     * Create new rental
     */
    public boolean createRental(Rental rental) throws SQLException {
        String sql = "INSERT INTO rentals (rental_code, equipment_id, customer_id, branch_id, " +
                     "reservation_id, start_date, end_date, daily_rate, rental_amount, " +
                     "security_deposit, membership_discount, long_rental_discount, " +
                     "final_payable_amount, payment_status, rental_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, rental.getRentalCode());
            stmt.setInt(2, rental.getEquipmentId());
            stmt.setInt(3, rental.getCustomerId());
            stmt.setInt(4, rental.getBranchId());
            stmt.setObject(5, rental.getReservationId());
            stmt.setDate(6, java.sql.Date.valueOf(rental.getStartDate()));
            stmt.setDate(7, java.sql.Date.valueOf(rental.getEndDate()));
            stmt.setBigDecimal(8, rental.getDailyRate());
            stmt.setBigDecimal(9, rental.getRentalAmount());
            stmt.setBigDecimal(10, rental.getSecurityDeposit());
            stmt.setBigDecimal(11, rental.getMembershipDiscount());
            stmt.setBigDecimal(12, rental.getLongRentalDiscount());
            stmt.setBigDecimal(13, rental.getFinalPayableAmount());
            stmt.setString(14, rental.getPaymentStatus().toString());
            stmt.setString(15, rental.getRentalStatus().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rental.setRentalId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Update rental status
     */
    public boolean updateRentalStatus(int rentalId, Rental.RentalStatus status) throws SQLException {
        String sql = "UPDATE rentals SET rental_status = ? WHERE rental_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setInt(2, rentalId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateRentalWithReservationId(int rentalId, int reservationId) throws SQLException {
        String sql = "UPDATE rentals SET reservation_id = ? WHERE rental_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            stmt.setInt(2, rentalId);

            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updatePaymentStatus(int rentalId, Rental.PaymentStatus status) throws SQLException {
        String sql = "UPDATE rentals SET payment_status = ? WHERE rental_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.toString());
            stmt.setInt(2, rentalId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update rental with return information
     */
    public boolean updateRentalReturn(int rentalId, LocalDate actualReturnDate, 
                                     Rental.RentalStatus status) throws SQLException {
        String sql = "UPDATE rentals SET actual_return_date = ?, rental_status = ? WHERE rental_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(actualReturnDate));
            stmt.setString(2, status.toString());
            stmt.setInt(3, rentalId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Rental object
     */
    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setRentalId(rs.getInt("rental_id"));
        rental.setRentalCode(rs.getString("rental_code"));
        rental.setEquipmentId(rs.getInt("equipment_id"));
        rental.setCustomerId(rs.getInt("customer_id"));
        rental.setBranchId(rs.getInt("branch_id"));
        rental.setReservationId((Integer) rs.getObject("reservation_id"));
        rental.setStartDate(rs.getDate("start_date").toLocalDate());
        rental.setEndDate(rs.getDate("end_date").toLocalDate());
        
        Date actualReturn = rs.getDate("actual_return_date");
        if (actualReturn != null) {
            rental.setActualReturnDate(actualReturn.toLocalDate());
        }
        
        rental.setDailyRate(rs.getBigDecimal("daily_rate"));
        rental.setRentalAmount(rs.getBigDecimal("rental_amount"));
        rental.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        rental.setMembershipDiscount(rs.getBigDecimal("membership_discount"));
        rental.setLongRentalDiscount(rs.getBigDecimal("long_rental_discount"));
        rental.setFinalPayableAmount(rs.getBigDecimal("final_payable_amount"));
        rental.setPaymentStatus(Rental.PaymentStatus.valueOf(rs.getString("payment_status")));
        rental.setRentalStatus(Rental.RentalStatus.valueOf(rs.getString("rental_status")));
        
        // Join fields
        rental.setEquipmentDetails(rs.getString("brand") + " " + rs.getString("model"));
        rental.setCustomerName(rs.getString("customer_name"));
        rental.setBranchName(rs.getString("branch_name"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            rental.setCreatedAt(ts.toLocalDateTime());
        }
        
        return rental;
    }

    public ResultSet getBranchRevenueReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT b.branch_name, " +
                     "COUNT(r.rental_id) as total_rentals, " +
                     "SUM(r.final_payable_amount) as total_revenue, " +
                     "SUM(rd.late_fee) as total_late_fees, " +
                     "SUM(rd.damage_charge) as total_damage_charges " +
                     "FROM rentals r " +
                     "JOIN branches b ON r.branch_id = b.branch_id " +
                     "LEFT JOIN return_details rd ON r.rental_id = rd.rental_id " +
                     "WHERE r.start_date <= ? AND r.end_date >= ? " +
                     "GROUP BY b.branch_name";

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, java.sql.Date.valueOf(startDate));
        stmt.setDate(2, java.sql.Date.valueOf(endDate));

        return stmt.executeQuery();
    }

    public ResultSet getEquipmentUtilizationReport(int branchId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT e.equipment_code, e.brand, e.model, " +
                     "SUM(DATEDIFF(r.end_date, r.start_date)) as days_rented, " +
                     "DATEDIFF(?, ?) as total_days_in_period " +
                     "FROM rentals r " +
                     "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                     "WHERE r.branch_id = ? AND r.start_date <= ? AND r.end_date >= ? " +
                     "GROUP BY e.equipment_id";

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, java.sql.Date.valueOf(endDate));
        stmt.setDate(2, java.sql.Date.valueOf(startDate));
        stmt.setInt(3, branchId);
        stmt.setDate(4, java.sql.Date.valueOf(startDate));
        stmt.setDate(5, java.sql.Date.valueOf(endDate));

        return stmt.executeQuery();
    }
}