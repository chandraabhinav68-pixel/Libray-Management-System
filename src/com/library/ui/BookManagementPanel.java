package com.library.ui;

import com.library.model.LibraryItem;
import com.library.model.Book;
import com.library.model.EBook;
import com.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private final LibraryService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public BookManagementPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // Toolbar with buttons & search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Item");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search Catalog:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        topPanel.add(btnPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Title", "Author", "Category", "Type", "Total", "Available", "Extra Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Event listeners
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> loadData());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<LibraryItem> items = service.getAllItems();
            for (LibraryItem item : items) {
                String extra = "";
                if (item instanceof Book b) extra = "ISBN: " + b.getIsbn() + " (" + b.getPages() + "p)";
                else if (item instanceof EBook eb) extra = "Size: " + eb.getFileSizeMb() + "MB";

                tableModel.addRow(new Object[]{
                        item.getItemId(),
                        item.getTitle(),
                        item.getAuthor(),
                        item.getCategory(),
                        item.getItemType(),
                        item.getTotalCopies(),
                        item.getAvailableCopies(),
                        extra
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        AddEditBookDialog dialog = new AddEditBookDialog(parent, "Add New Library Item", null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                service.getBookDAO().save(dialog.getItem());
                service.refreshCache();
                loadData();
                JOptionPane.showMessageDialog(this, "Item added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);

        try {
            LibraryItem item = service.getBookDAO().findById(id);
            if (item != null) {
                Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
                AddEditBookDialog dialog = new AddEditBookDialog(parent, "Edit Library Item", item);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.getBookDAO().update(dialog.getItem());
                    service.refreshCache();
                    loadData();
                    JOptionPane.showMessageDialog(this, "Item updated successfully!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item '" + id + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.getBookDAO().delete(id);
                service.refreshCache();
                loadData();
                JOptionPane.showMessageDialog(this, "Item deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
