package com.library.model;

import java.time.LocalDate;

/**
 * Model representing a book issue/return transaction record.
 */
public class Transaction {
    private String transactionId;
    private String userId;
    private String itemId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private String status; // ISSUED, RETURNED, OVERDUE

    public Transaction(String transactionId, String userId, String itemId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, double fineAmount, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.itemId = itemId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
