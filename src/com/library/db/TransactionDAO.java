package com.library.db;

import com.library.interfaces.GenericDAO;
import com.library.model.Transaction;
import com.library.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transactions (Issue & Return Records).
 */
public class TransactionDAO implements GenericDAO<Transaction, String> {

    @Override
    public void save(Transaction tx) throws DatabaseException {
        String sql = "INSERT INTO transactions (transaction_id, user_id, item_id, issue_date, due_date, return_date, fine_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tx.getTransactionId());
            pstmt.setString(2, tx.getUserId());
            pstmt.setString(3, tx.getItemId());
            pstmt.setString(4, tx.getIssueDate() != null ? tx.getIssueDate().toString() : null);
            pstmt.setString(5, tx.getDueDate() != null ? tx.getDueDate().toString() : null);
            pstmt.setString(6, tx.getReturnDate() != null ? tx.getReturnDate().toString() : null);
            pstmt.setDouble(7, tx.getFineAmount());
            pstmt.setString(8, tx.getStatus());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error saving transaction: " + tx.getTransactionId(), e);
        }
    }

    @Override
    public Transaction findById(String id) throws DatabaseException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding transaction by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() throws DatabaseException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY issue_date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading all transactions", e);
        }
        return list;
    }

    @Override
    public void update(Transaction tx) throws DatabaseException {
        String sql = "UPDATE transactions SET user_id = ?, item_id = ?, issue_date = ?, due_date = ?, return_date = ?, fine_amount = ?, status = ? WHERE transaction_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tx.getUserId());
            pstmt.setString(2, tx.getItemId());
            pstmt.setString(3, tx.getIssueDate() != null ? tx.getIssueDate().toString() : null);
            pstmt.setString(4, tx.getDueDate() != null ? tx.getDueDate().toString() : null);
            pstmt.setString(5, tx.getReturnDate() != null ? tx.getReturnDate().toString() : null);
            pstmt.setDouble(6, tx.getFineAmount());
            pstmt.setString(7, tx.getStatus());
            pstmt.setString(8, tx.getTransactionId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating transaction: " + tx.getTransactionId(), e);
        }
    }

    @Override
    public void delete(String id) throws DatabaseException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting transaction: " + id, e);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        String id = rs.getString("transaction_id");
        String uid = rs.getString("user_id");
        String iid = rs.getString("item_id");
        String idate = rs.getString("issue_date");
        String ddate = rs.getString("due_date");
        String rdate = rs.getString("return_date");
        double fine = rs.getDouble("fine_amount");
        String status = rs.getString("status");

        return new Transaction(
                id,
                uid,
                iid,
                idate != null ? LocalDate.parse(idate) : null,
                ddate != null ? LocalDate.parse(ddate) : null,
                rdate != null ? LocalDate.parse(rdate) : null,
                fine,
                status
        );
    }
}
