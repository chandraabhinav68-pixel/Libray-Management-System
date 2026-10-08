package com.library.service;

import com.library.model.Transaction;
import com.library.model.LibraryItem;
import com.library.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

/**
 * Background Runnable Task for dynamic fine calculations.
 * Demonstrates Multithreading & Synchronization in Java.
 */
public class FineCalculationTask implements Runnable {
    private final LibraryService service;
    private final Consumer<String> statusListener;

    public FineCalculationTask(LibraryService service, Consumer<String> statusListener) {
        this.service = service;
        this.statusListener = statusListener;
    }

    @Override
    public void run() {
        try {
            notifyStatus("Starting background fine calculation sweep...");
            Thread.sleep(1000); // Simulate background processing delay

            List<Transaction> transactions = service.getAllTransactions();
            int updatedCount = 0;
            double totalFinesAccumulated = 0.0;

            LocalDate today = LocalDate.now();

            for (Transaction tx : transactions) {
                if ("ISSUED".equalsIgnoreCase(tx.getStatus()) && tx.getDueDate() != null && today.isAfter(tx.getDueDate())) {
                    long daysOverdue = ChronoUnit.DAYS.between(tx.getDueDate(), today);

                    LibraryItem item = service.getBookDAO().findById(tx.getItemId());
                    User user = service.getUserDAO().findById(tx.getUserId());

                    double rawFine = item != null ? item.calculateOverdueFine((int) daysOverdue) : daysOverdue * 1.5;
                    double finalFine = user != null ? user.calculateDiscountedFine(rawFine) : rawFine;

                    tx.setFineAmount(finalFine);
                    tx.setStatus("OVERDUE");
                    service.getTransactionDAO().update(tx);

                    updatedCount++;
                    totalFinesAccumulated += finalFine;
                }
            }

            notifyStatus(String.format("Background Fine Sweep Completed! Updated %d overdue record(s). Total Outstanding Fines: $%.2f", updatedCount, totalFinesAccumulated));

        } catch (InterruptedException e) {
            notifyStatus("Fine calculation task interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            notifyStatus("Error running fine calculation task: " + e.getMessage());
        }
    }

    private void notifyStatus(String message) {
        if (statusListener != null) {
            statusListener.accept(message);
        }
    }
}
