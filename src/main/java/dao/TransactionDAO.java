package dao;

import model.Transaction;
import model.TransactionType;
import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Create a new transaction
    public boolean createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (product_id, transaction_type, quantity, " +
                "unit_price, total_amount) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getProductId());
            stmt.setString(2, transaction.getType().name());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setDouble(4, transaction.getUnitPrice());
            stmt.setDouble(5, transaction.getTotalAmount());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = createTransactionFromResultSet(rs);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    // Get transactions by product
    public List<Transaction> getTransactionsByProduct(int productId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE product_id = ? ORDER BY transaction_date DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = createTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setProductId(rs.getInt("product_id"));
        transaction.setType(TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setQuantity(rs.getInt("quantity"));
        transaction.setUnitPrice(rs.getDouble("unit_price"));
        transaction.setTotalAmount(rs.getDouble("total_amount"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
        return transaction;
    }
}
