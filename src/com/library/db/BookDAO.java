package com.library.db;

import com.library.interfaces.GenericDAO;
import com.library.model.LibraryItem;
import com.library.model.Book;
import com.library.model.EBook;
import com.library.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Library Items (Books & E-Books).
 * Implements GenericDAO interface. Demonstrates JDBC PreparedStatement usage.
 */
public class BookDAO implements GenericDAO<LibraryItem, String> {

    @Override
    public void save(LibraryItem item) throws DatabaseException {
        String sql = "INSERT INTO items (item_id, title, author, category, total_copies, available_copies, item_type, extra_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemId());
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, item.getAuthor());
            pstmt.setString(4, item.getCategory());
            pstmt.setInt(5, item.getTotalCopies());
            pstmt.setInt(6, item.getAvailableCopies());
            pstmt.setString(7, item.getItemType());
            pstmt.setString(8, extractExtraInfo(item));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting item into database: " + item.getItemId(), e);
        }
    }

    @Override
    public LibraryItem findById(String id) throws DatabaseException {
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding item by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<LibraryItem> findAll() throws DatabaseException {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading all items from database", e);
        }
        return items;
    }

    @Override
    public void update(LibraryItem item) throws DatabaseException {
        String sql = "UPDATE items SET title = ?, author = ?, category = ?, total_copies = ?, available_copies = ?, item_type = ?, extra_info = ? WHERE item_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getAuthor());
            pstmt.setString(3, item.getCategory());
            pstmt.setInt(4, item.getTotalCopies());
            pstmt.setInt(5, item.getAvailableCopies());
            pstmt.setString(6, item.getItemType());
            pstmt.setString(7, extractExtraInfo(item));
            pstmt.setString(8, item.getItemId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating item: " + item.getItemId(), e);
        }
    }

    @Override
    public void delete(String id) throws DatabaseException {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting item: " + id, e);
        }
    }

    private String extractExtraInfo(LibraryItem item) {
        if (item instanceof Book book) {
            return "ISBN: " + book.getIsbn() + "|Pages: " + book.getPages();
        } else if (item instanceof EBook eBook) {
            return "URL: " + eBook.getDownloadUrl() + "|Size: " + eBook.getFileSizeMb() + "MB";
        }
        return "";
    }

    private LibraryItem mapResultSetToItem(ResultSet rs) throws SQLException {
        String id = rs.getString("item_id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String category = rs.getString("category");
        int total = rs.getInt("total_copies");
        int avail = rs.getInt("available_copies");
        String type = rs.getString("item_type");
        String extra = rs.getString("extra_info");

        if ("EBOOK".equalsIgnoreCase(type)) {
            String url = "https://library.org/download";
            double size = 5.0;
            if (extra != null && extra.contains("URL: ")) {
                String[] parts = extra.split("\\|");
                for (String p : parts) {
                    if (p.startsWith("URL: ")) url = p.substring(5);
                    if (p.startsWith("Size: ")) {
                        try { size = Double.parseDouble(p.substring(6).replace("MB", "")); } catch (Exception ignored) {}
                    }
                }
            }
            return new EBook(id, title, author, category, total, avail, url, size);
        } else {
            String isbn = "978-0000000000";
            int pages = 300;
            if (extra != null && extra.contains("ISBN: ")) {
                String[] parts = extra.split("\\|");
                for (String p : parts) {
                    if (p.startsWith("ISBN: ")) isbn = p.substring(6);
                    if (p.startsWith("Pages: ")) {
                        try { pages = Integer.parseInt(p.substring(7)); } catch (Exception ignored) {}
                    }
                }
            }
            return new Book(id, title, author, category, total, avail, isbn, pages);
        }
    }
}
