package com.library.ui;

import com.library.service.LibraryService;
import com.library.service.FineCalculationTask;
import com.library.service.BackupTask;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AnalyticsPanel extends JPanel {
    private final LibraryService service;
    private final ExecutorService executorService;

    private JLabel totalBooksLabel, totalUsersLabel, activeIssuesLabel, totalFinesLabel;
    private JTextArea logArea;
    private JProgressBar progressBar;

    public AnalyticsPanel(LibraryService service) {
        this.service = service;
        this.executorService = Executors.newFixedThreadPool(4);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        refreshStats();
    }

    private void initUI() {
        // Summary Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));

        totalBooksLabel = createCard(cardsPanel, "Total Inventory Items", "0", new Color(41, 128, 185));
        totalUsersLabel = createCard(cardsPanel, "Registered Users", "0", new Color(39, 174, 96));
        activeIssuesLabel = createCard(cardsPanel, "Active Borrowed Books", "0", new Color(230, 126, 34));
        totalFinesLabel = createCard(cardsPanel, "Accumulated Fines", "$0.00", new Color(142, 68, 173));

        add(cardsPanel, BorderLayout.NORTH);

        // Center Panel: Multithreading Control & Logs
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Background Worker Tasks & Multithreading Dashboard"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton runFineThreadBtn = new JButton("Run Background Fine Sweep (Runnable)");
        JButton runBackupThreadBtn = new JButton("Export Catalog Backup (Callable/Future)");
        JButton refreshStatsBtn = new JButton("Refresh Overview Metrics");

        btnPanel.add(runFineThreadBtn);
        btnPanel.add(runBackupThreadBtn);
        btnPanel.add(refreshStatsBtn);

        centerPanel.add(btnPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(245, 247, 250));
        logArea.append("[SYSTEM] Background Worker Task Console Ready.\n");

        centerPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        centerPanel.add(progressBar, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Event listeners
        runFineThreadBtn.addActionListener(e -> startFineSweepThread());
        runBackupThreadBtn.addActionListener(e -> startBackupThread());
        refreshStatsBtn.addActionListener(e -> refreshStats());
    }

    private JLabel createCard(JPanel container, String title, String initialVal, Color headerBg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setOpaque(true);
        titleLbl.setBackground(headerBg);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel valLbl = new JLabel(initialVal, SwingConstants.CENTER);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        valLbl.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        container.add(card);

        return valLbl;
    }

    public void refreshStats() {
        try {
            int books = service.getAllItems().size();
            int users = service.getAllUsers().size();
            var txs = service.getAllTransactions();

            int active = 0;
            double fines = 0.0;
            for (var tx : txs) {
                if ("ISSUED".equalsIgnoreCase(tx.getStatus()) || "OVERDUE".equalsIgnoreCase(tx.getStatus())) {
                    active++;
                }
                fines += tx.getFineAmount();
            }

            totalBooksLabel.setText(String.valueOf(books));
            totalUsersLabel.setText(String.valueOf(users));
            activeIssuesLabel.setText(String.valueOf(active));
            totalFinesLabel.setText(String.format("$%.2f", fines));
            appendLog("[INFO] Dashboard metrics updated successfully.");
        } catch (Exception e) {
            appendLog("[ERROR] Failed to calculate stats: " + e.getMessage());
        }
    }

    private void startFineSweepThread() {
        progressBar.setIndeterminate(true);
        appendLog("[THREAD-START] Launching FineCalculationTask Runnable in background thread pool...");

        executorService.submit(new FineCalculationTask(service, msg -> SwingUtilities.invokeLater(() -> {
            appendLog("[FINE-THREAD] " + msg);
            if (msg.contains("Completed")) {
                progressBar.setIndeterminate(false);
                refreshStats();
            }
        })));
    }

    private void startBackupThread() {
        progressBar.setIndeterminate(true);
        String backupPath = "catalog_backup_" + System.currentTimeMillis() + ".csv";
        appendLog("[THREAD-START] Executing BackupTask Callable on worker thread -> File: " + backupPath);

        executorService.submit(() -> {
            try {
                BackupTask task = new BackupTask(service, backupPath);
                Future<Boolean> future = executorService.submit(task);
                Boolean success = future.get(); // Blocking wait on Future in worker thread

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    if (success) {
                        File f = new File(backupPath);
                        appendLog("[BACKUP-THREAD] Catalog successfully exported! File size: " + f.length() + " bytes at " + f.getAbsolutePath());
                        JOptionPane.showMessageDialog(this, "Catalog exported successfully to:\n" + f.getAbsolutePath(), "Backup Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    appendLog("[ERROR] Backup thread failed: " + ex.getMessage());
                });
            }
        });
    }

    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
