package com.gearrentpro.dao;

import com.gearrentpro.entity.Customer;
import com.gearrentpro.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    /**
     * Get all active customers
     */
    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers WHERE status = 'ACTIVE' ORDER BY customer_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get customer by code
     */
    public Customer getCustomerByCode(String customerCode) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get customer by NIC/Passport
     */
    public Customer getCustomerByNIC(String nicPassport) throws SQLException {
        String sql = "SELECT * FROM customers WHERE nic_passport = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nicPassport);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new customer
     */
    public int createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_code, customer_name, nic_passport, contact_number, " +
                     "email, address, membership_level, deposit_limit, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getCustomerCode());
            stmt.setString(2, customer.getCustomerName());
            stmt.setString(3, customer.getNicPassport());
            stmt.setString(4, customer.getContactNumber());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getMembershipLevel().toString());
            stmt.setBigDecimal(8, customer.getDepositLimit());
            stmt.setString(9, customer.getStatus().toString());
            
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
     * Update customer
     */
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET customer_code = ?, customer_name = ?, nic_passport = ?, " +
                     "contact_number = ?, email = ?, address = ?, membership_level = ?, " +
                     "deposit_limit = ?, status = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getCustomerCode());
            stmt.setString(2, customer.getCustomerName());
            stmt.setString(3, customer.getNicPassport());
            stmt.setString(4, customer.getContactNumber());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getMembershipLevel().toString());
            stmt.setBigDecimal(8, customer.getDepositLimit());
            stmt.setString(9, customer.getStatus().toString());
            stmt.setInt(10, customer.getCustomerId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Helper method to map ResultSet to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerCode(rs.getString("customer_code"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setNicPassport(rs.getString("nic_passport"));
        customer.setContactNumber(rs.getString("contact_number"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        customer.setMembershipLevel(Customer.MembershipLevel.valueOf(rs.getString("membership_level")));
        customer.setDepositLimit(rs.getBigDecimal("deposit_limit"));
        customer.setStatus(Customer.CustomerStatus.valueOf(rs.getString("status")));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            customer.setCreatedAt(ts.toLocalDateTime());
        }
        
        return customer;
    }
}