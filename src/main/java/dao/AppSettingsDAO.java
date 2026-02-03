package dao;

import model.*;
import db.DatabaseConnection;

import java.sql.*;

public class AppSettingsDAO {

    // Get current app settings
    public AppSettings getAppSettings() throws SQLException {
        String sql = "SELECT * FROM app_settings ORDER BY id DESC LIMIT 1";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                AppSettings settings = new AppSettings();
                settings.setId(rs.getInt("id"));
                settings.setInitialCapital(rs.getDouble("initial_capital"));
                settings.setCurrentCapital(rs.getDouble("current_capital"));
                settings.setTotalIncome(rs.getDouble("total_income"));
                settings.setTotalCost(rs.getDouble("total_cost"));
                return settings;
            }
        }
        return null;
    }

    // Update app settings
    public boolean updateAppSettings(AppSettings settings) throws SQLException {
        String sql = "UPDATE app_settings SET current_capital = ?, total_income = ?, " +
                "total_cost = ? WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, settings.getCurrentCapital());
            stmt.setDouble(2, settings.getTotalIncome());
            stmt.setDouble(3, settings.getTotalCost());
            stmt.setInt(4, settings.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Process a purchase transaction
    public boolean processPurchase(int productId, int quantity, double unitPrice) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Get current settings
            AppSettings settings = getAppSettings();
            double totalCost = quantity * unitPrice;

            // Check if we have enough capital
            if (settings.getCurrentCapital() < totalCost) {
                conn.rollback();
                return false;
            }

            // Update product stock
            ProductDAO productDAO = new ProductDAO();
            String getStockSql = "SELECT stock_quantity FROM products WHERE id = ?";
            int currentStock = 0;

            try (PreparedStatement stmt = conn.prepareStatement(getStockSql)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentStock = rs.getInt("stock_quantity");
                }
            }

            productDAO.updateStock(productId, currentStock + quantity);

            // Create transaction record
            TransactionDAO transactionDAO = new TransactionDAO();
            Transaction transaction = new Transaction(productId,
                    model.TransactionType.PURCHASE,
                    quantity, unitPrice, totalCost);
            transactionDAO.createTransaction(transaction);

            // Update app settings
            settings.setTotalCost(settings.getTotalCost() + totalCost);
            settings.setCurrentCapital(settings.getCurrentCapital() - totalCost);
            updateAppSettings(settings);

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw new SQLException("Purchase transaction failed", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Process a sale transaction
    public boolean processSale(int productId, int quantity, double unitPrice) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Get current settings
            AppSettings settings = getAppSettings();
            double totalIncome = quantity * unitPrice;

            // Check if we have enough stock
            String getStockSql = "SELECT stock_quantity FROM products WHERE id = ?";
            int currentStock = 0;

            try (PreparedStatement stmt = conn.prepareStatement(getStockSql)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentStock = rs.getInt("stock_quantity");
                }
            }

            if (currentStock < quantity) {
                conn.rollback();
                return false;
            }

            // Update product stock
            ProductDAO productDAO = new ProductDAO();
            productDAO.updateStock(productId, currentStock - quantity);

            // Create transaction record
            TransactionDAO transactionDAO = new TransactionDAO();
            Transaction transaction = new Transaction(productId,
                    model.TransactionType.SALE,
                    quantity, unitPrice, totalIncome);
            transactionDAO.createTransaction(transaction);

            // Update app settings
            settings.setTotalIncome(settings.getTotalIncome() + totalIncome);
            settings.setCurrentCapital(settings.getCurrentCapital() + totalIncome);
            updateAppSettings(settings);

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw new SQLException("Sale transaction failed", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
