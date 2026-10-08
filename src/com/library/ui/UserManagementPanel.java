package com.library.ui;

import com.library.model.User;
import com.library.model.StudentUser;
import com.library.model.FacultyUser;
import com.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final LibraryService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserManagementPanel(LibraryService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addStudentBtn = new JButton("Add Student");
        JButton addFacultyBtn = new JButton("Add Faculty");
        JButton refreshBtn = new JButton("Refresh Users");

        topPanel.add(addStudentBtn);
        topPanel.add(addFacultyBtn);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"User ID", "Name", "Email", "Phone", "Role", "Borrowed Count", "Max Quota", "Extra Details"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addStudentBtn.addActionListener(e -> onAddUser("STUDENT"));
        addFacultyBtn.addActionListener(e -> onAddUser("FACULTY"));
        refreshBtn.addActionListener(e -> loadData());
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<User> users = service.getAllUsers();
            for (User u : users) {
                String extra = u instanceof StudentUser s ? "Student ID: " + s.getStudentIdNumber() : (u instanceof FacultyUser f ? "Dept: " + f.getDepartment() : "");
                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getUserRole(),
                        u.getCurrentBorrowedCount(),
                        u.getMaxBorrowLimit(),
                        extra
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddUser(String role) {
        JTextField idF = new JTextField("U" + (System.currentTimeMillis() % 10000));
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JTextField phoneF = new JTextField();
        JTextField extraF = new JTextField();

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("User ID:")); form.add(idF);
        form.add(new JLabel("Full Name:")); form.add(nameF);
        form.add(new JLabel("Email:")); form.add(emailF);
        form.add(new JLabel("Phone:")); form.add(phoneF);
        form.add(new JLabel("FACULTY".equals(role) ? "Department:" : "Student Reg No:")); form.add(extraF);

        int result = JOptionPane.showConfirmDialog(this, form, "Add New " + role, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                User user;
                if ("FACULTY".equals(role)) {
                    user = new FacultyUser(idF.getText().trim(), nameF.getText().trim(), emailF.getText().trim(), phoneF.getText().trim(), 0, extraF.getText().trim());
                } else {
                    user = new StudentUser(idF.getText().trim(), nameF.getText().trim(), emailF.getText().trim(), phoneF.getText().trim(), 0, extraF.getText().trim());
                }
                service.getUserDAO().save(user);
                service.refreshCache();
                loadData();
                JOptionPane.showMessageDialog(this, role + " added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
