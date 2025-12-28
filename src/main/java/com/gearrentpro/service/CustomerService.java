package com.gearrentpro.service;

import com.gearrentpro.dao.CustomerDAO;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;
    private static CustomerService instance;
    
    private CustomerService() {
        this.customerDAO = new CustomerDAO();
    }
    
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }
    
    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) throws SQLException {
        return customerDAO.getCustomerById(customerId);
    }
    
    /**
     * Get customer by code
     */
    public Customer getCustomerByCode(String customerCode) throws SQLException {
        return customerDAO.getCustomerByCode(customerCode);
    }
    
    /**
     * Create new customer with validation
     */
    public boolean createCustomer(Customer customer) throws SQLException {
        validateCustomer(customer);
        
        // Check if NIC/Passport already exists
        if (customerDAO.getCustomerByNIC(customer.getNicPassport()) != null) {
            throw new IllegalArgumentException("Customer with this NIC/Passport already exists!");
        }
        
        int customerId = customerDAO.createCustomer(customer);
        return customerId > 0;
    }
    
    /**
     * Update customer
     */
    public boolean updateCustomer(Customer customer) throws SQLException {
        validateCustomer(customer);
        return customerDAO.updateCustomer(customer);
    }
    
    /**
     * Validate customer data
     */
    private void validateCustomer(Customer customer) throws IllegalArgumentException {
        if (ValidationUtils.isNullOrEmpty(customer.getCustomerName())) {
            throw new IllegalArgumentException("Customer name is required!");
        }
        
        if (!ValidationUtils.isValidNIC(customer.getNicPassport())) {
            throw new IllegalArgumentException("Valid NIC/Passport is required!");
        }
        
        if (!ValidationUtils.isValidPhoneNumber(customer.getContactNumber())) {
            throw new IllegalArgumentException("Valid phone number is required!");
        }
        
        if (!ValidationUtils.isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Valid email is required!");
        }
    }
}