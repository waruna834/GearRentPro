package com.gearrentpro.service;

import com.gearrentpro.dao.RentalDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportService {

    private RentalDAO rentalDAO;
    private static ReportService instance;

    private ReportService() {
        this.rentalDAO = new RentalDAO();
    }

    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public ResultSet getBranchRevenueReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        return rentalDAO.getBranchRevenueReport(startDate, endDate);
    }

    public ResultSet getEquipmentUtilizationReport(int branchId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return rentalDAO.getEquipmentUtilizationReport(branchId, startDate, endDate);
    }
}
