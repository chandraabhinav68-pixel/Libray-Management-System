package com.library.ui;

import com.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final LibraryService service;
    private BookManagementPanel bookPanel;
    private UserManagementPanel userPanel;
    private IssueReturnPanel issueReturnPanel;
    private AnalyticsPanel analyticsPanel;

    public MainFrame() {
        super("Library Management System - OOP & JDBC GUI Dashboard");
        this.service = new LibraryService();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        // Styling Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Layout setup
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Java Swing GUI | SQLite JDBC | Multithreaded Architecture");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(189, 195, 199));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        bookPanel = new BookManagementPanel(service);
        userPanel = new UserManagementPanel(service);
        issueReturnPanel = new IssueReturnPanel(service);
        analyticsPanel = new AnalyticsPanel(service);

        tabbedPane.addTab("Catalog Management", bookPanel);
        tabbedPane.addTab("User Directory", userPanel);
        tabbedPane.addTab("Issue / Return Books", issueReturnPanel);
        tabbedPane.addTab("Analytics & Multithreading", analyticsPanel);

        // Listen for tab switches to refresh data dynamically
        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) bookPanel.loadData();
            else if (idx == 1) userPanel.loadData();
            else if (idx == 2) issueReturnPanel.loadData();
            else if (idx == 3) analyticsPanel.refreshStats();
        });

        add(tabbedPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Connected to SQLite Database | System Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }
}
