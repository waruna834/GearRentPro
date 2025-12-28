package com.gearrentpro.service;

import com.gearrentpro.dao.UserDAO;
import com.gearrentpro.entity.User;

import java.sql.SQLException;

public class AuthenticationService {
    private UserDAO userDAO;
    private static AuthenticationService instance;
    private User currentUser;
    
    private AuthenticationService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Singleton pattern - get instance
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    /**
     * Login user with username and password
     */
    public boolean login(String username, String password) throws SQLException {
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current user role
     */
    public User.UserRole getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return null;
    }
    
    /**
     * Get current user branch ID (null for Admin)
     */
    public Integer getCurrentUserBranchId() {
        if (currentUser != null) {
            return currentUser.getBranchId();
        }
        return null;
    }
    
    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.UserRole.ADMIN;
    }
    
    /**
     * Check if user has branch manager role
     */
    public boolean isBranchManager() {
        return currentUser != null && currentUser.getRole() == User.UserRole.BRANCH_MANAGER;
    }
    
    /**
     * Check if user has staff role
     */
    public boolean isStaff() {
        return currentUser != null && currentUser.getRole() == User.UserRole.STAFF;
    }
}