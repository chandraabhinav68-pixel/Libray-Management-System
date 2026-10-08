package com.library.ui;

import com.library.model.Transaction;
import com.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IssueReturnPanel extends JPanel {
    private final LibraryService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField userIdField, itemIdField;

    public IssueReturnPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // Issue Book Form Bar
        JPanel issueFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        issueFormPanel.setBorder(BorderFactory.createTitledBorder("Issue Book / Item"));

        issueFormPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField(8);
        issueFormPanel.add(userIdField);

        issueFormPanel.add(new JLabel("Item ID:"));
        itemIdField = new JTextField(8);
        issueFormPanel.add(itemIdField);

        JButton issueBtn = new JButton("Issue Book");
        JButton returnSelectedBtn = new JButton("Return Selected Book");
        JButton refreshBtn = new JButton("Refresh Transactions");

        issueFormPanel.add(issueBtn);
        issueFormPanel.add(returnSelectedBtn);
        issueFormPanel.add(refreshBtn);

        add(issueFormPanel, BorderLayout.NORTH);

        // Transactions Table
        String[] cols = {"Transaction ID", "User ID", "Item ID", "Issue Date", "Due Date", "Return Date", "Fine ($)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Action Handlers
        issueBtn.addActionListener(e -> onIssueBook());
        returnSelectedBtn.addActionListener(e -> onReturnBook());
        refreshBtn.addActionListener(e -> loadData());
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Transaction> list = service.getAllTransactions();
            for (Transaction tx : list) {
                tableModel.addRow(new Object[]{
                        tx.getTransactionId(),
                        tx.getUserId(),
                        tx.getItemId(),
                        tx.getIssueDate(),
                        tx.getDueDate(),
                        tx.getReturnDate() != null ? tx.getReturnDate() : "N/A",
                        String.format("%.2f", tx.getFineAmount()),
                        tx.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onIssueBook() {
        String uid = userIdField.getText().trim();
        String iid = itemIdField.getText().trim();

        if (uid.isEmpty() || iid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Item ID!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Transaction tx = service.issueBook(uid, iid);
            userIdField.setText("");
            itemIdField.setText("");
            loadData();
            JOptionPane.showMessageDialog(this, "Book issued successfully!\nTransaction ID: " + tx.getTransactionId() + "\nDue Date: " + tx.getDueDate(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to issue book: " + ex.getMessage(), "Issue Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onReturnBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a transaction row from the table to return!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String txId = (String) tableModel.getValueAt(row, 0);
        try {
            double fine = service.returnBook(txId);
            loadData();
            String msg = "Book returned successfully!";
            if (fine > 0) {
                msg += String.format("\nOverdue Fine Applicable: $%.2f", fine);
            } else {
                msg += "\nNo fine incurred.";
            }
            JOptionPane.showMessageDialog(this, msg, "Return Processed", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to return book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
