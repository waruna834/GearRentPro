package com.gearrentpro.service;

import com.gearrentpro.dao.BranchDAO;
import com.gearrentpro.entity.Branch;

import java.sql.SQLException;
import java.util.List;

public class BranchService {
    private BranchDAO branchDAO;
    private static BranchService instance;
    
    private BranchService() {
        this.branchDAO = new BranchDAO();
    }
    
    public static BranchService getInstance() {
        if (instance == null) {
            instance = new BranchService();
        }
        return instance;
    }
    
    /**
     * Get all branches
     */
    public List<Branch> getAllBranches() throws SQLException {
        return branchDAO.getAllBranches();
    }
    
    /**
     * Get branch by ID
     */
    public Branch getBranchById(int branchId) throws SQLException {
        return branchDAO.getBranchById(branchId);
    }
    
    /**
     * Create new branch
     */
    public boolean createBranch(Branch branch) throws SQLException {
        // Validate branch code is unique
        if (branchDAO.getBranchByCode(branch.getBranchCode()) != null) {
            throw new IllegalArgumentException("Branch code already exists!");
        }
        
        int branchId = branchDAO.createBranch(branch);
        return branchId > 0;
    }
    
    /**
     * Update branch
     */
    public boolean updateBranch(Branch branch) throws SQLException {
        return branchDAO.updateBranch(branch);
    }
}