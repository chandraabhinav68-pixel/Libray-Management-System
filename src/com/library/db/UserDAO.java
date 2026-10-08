package com.library.db;

import com.library.interfaces.GenericDAO;
import com.library.model.User;
import com.library.model.StudentUser;
import com.library.model.FacultyUser;
import com.library.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Users (Student & Faculty).
 * Implements GenericDAO interface with PreparedStatements.
 */
public class UserDAO implements GenericDAO<User, String> {

    @Override
    public void save(User user) throws DatabaseException {
        String sql = "INSERT INTO users (user_id, name, email, phone, borrowed_count, user_role, extra_info) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setInt(5, user.getCurrentBorrowedCount());
            pstmt.setString(6, user.getUserRole());
            pstmt.setString(7, user instanceof StudentUser stu ? stu.getStudentIdNumber() : (user instanceof FacultyUser fac ? fac.getDepartment() : ""));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user to database: " + user.getUserId(), e);
        }
    }

    @Override
    public User findById(String id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading users from database", e);
        }
        return users;
    }

    @Override
    public void update(User user) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, borrowed_count = ?, user_role = ?, extra_info = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, user.getCurrentBorrowedCount());
            pstmt.setString(5, user.getUserRole());
            pstmt.setString(6, user instanceof StudentUser stu ? stu.getStudentIdNumber() : (user instanceof FacultyUser fac ? fac.getDepartment() : ""));
            pstmt.setString(7, user.getUserId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user: " + user.getUserId(), e);
        }
    }

    @Override
    public void delete(String id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user: " + id, e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        int count = rs.getInt("borrowed_count");
        String role = rs.getString("user_role");
        String extra = rs.getString("extra_info");

        if ("FACULTY".equalsIgnoreCase(role)) {
            return new FacultyUser(id, name, email, phone, count, extra != null ? extra : "General");
        } else {
            return new StudentUser(id, name, email, phone, count, extra != null ? extra : "STU-REG");
        }
    }
}
