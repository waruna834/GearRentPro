package com.gearrentpro.service;

import com.gearrentpro.dao.EquipmentDAO;
import com.gearrentpro.entity.Equipment;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EquipmentService {
    private EquipmentDAO equipmentDAO;
    private static EquipmentService instance;
    
    private EquipmentService() {
        this.equipmentDAO = new EquipmentDAO();
    }
    
    public static EquipmentService getInstance() {
        if (instance == null) {
            instance = new EquipmentService();
        }
        return instance;
    }
    
    /**
     * Get all equipment
     */
    public List<Equipment> getAllEquipment() throws SQLException {
        return equipmentDAO.getAllEquipment();
    }
    
    /**
     * Get equipment by ID
     */
    public Equipment getEquipmentById(int equipmentId) throws SQLException {
        return equipmentDAO.getEquipmentById(equipmentId);
    }
    
    /**
     * Get equipment by branch
     */
    public List<Equipment> getEquipmentByBranch(int branchId) throws SQLException {
        return equipmentDAO.getEquipmentByBranch(branchId);
    }
    
    /**
     * Get equipment by branch and category
     */
    public List<Equipment> getEquipmentByBranchAndCategory(int branchId, int categoryId) throws SQLException {
        return equipmentDAO.getEquipmentByBranchAndCategory(branchId, categoryId);
    }
    
    /**
     * Get available equipment for rental/reservation
     */
    public List<Equipment> getAvailableEquipment(int branchId, int categoryId) throws SQLException {
        return equipmentDAO.getAvailableEquipment(branchId, categoryId);
    }
    
    /**
     * Create new equipment with validation
     */
    public boolean createEquipment(Equipment equipment) throws SQLException {
        validateEquipment(equipment);
        
        int equipmentId = equipmentDAO.createEquipment(equipment);
        return equipmentId > 0;
    }
    
    /**
     * Update equipment
     */
    public boolean updateEquipment(Equipment equipment) throws SQLException {
        validateEquipment(equipment);
        return equipmentDAO.updateEquipment(equipment);
    }
    
    /**
     * Update equipment status
     */
    public boolean updateEquipmentStatus(int equipmentId, Equipment.EquipmentStatus status) throws SQLException {
        return equipmentDAO.updateEquipmentStatus(equipmentId, status);
    }
    
    public boolean isEquipmentAvailable(int equipmentId, java.time.LocalDate startDate, java.time.LocalDate endDate) throws SQLException {
        return equipmentDAO.isEquipmentAvailable(equipmentId, startDate, endDate);
    }

    /**
     * Validate equipment data
     */
    private void validateEquipment(Equipment equipment) throws IllegalArgumentException {
        if (equipment.getEquipmentCode() == null || equipment.getEquipmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment code is required!");
        }
        
        if (equipment.getBrand() == null || equipment.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required!");
        }
        
        if (equipment.getModel() == null || equipment.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required!");
        }
        
        if (equipment.getDailyBasePrice() == null || equipment.getDailyBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Daily base price must be greater than 0!");
        }
        
        if (equipment.getSecurityDeposit() == null || equipment.getSecurityDeposit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Security deposit must be greater than 0!");
        }
    }
}