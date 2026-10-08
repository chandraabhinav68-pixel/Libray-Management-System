package com.library.service;

import com.library.db.BookDAO;
import com.library.db.UserDAO;
import com.library.db.TransactionDAO;
import com.library.exceptions.*;
import com.library.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service orchestrating core Library Management business operations.
 * Demonstrates:
 * 1. Collections & Generics (List, Map, Queue)
 * 2. Multithreading & Thread Synchronization (ReentrantLock on transaction execution)
 * 3. Exception Handling (Throws domain custom exceptions)
 */
public class LibraryService {
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    // Collections for in-memory caching and fast lookups
    private final Map<String, LibraryItem> itemCache;
    private final Map<String, User> userCache;

    // Thread Safety: Lock for concurrent transaction safety
    private final ReentrantLock transactionLock = new ReentrantLock();

    public LibraryService() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        this.itemCache = new HashMap<>();
        this.userCache = new HashMap<>();
        refreshCache();
    }

    public synchronized void refreshCache() {
        try {
            itemCache.clear();
            userCache.clear();
            for (LibraryItem item : bookDAO.findAll()) {
                itemCache.put(item.getItemId(), item);
            }
            for (User user : userDAO.findAll()) {
                userCache.put(user.getUserId(), user);
            }
        } catch (DatabaseException e) {
            System.err.println("Failed to populate memory cache: " + e.getMessage());
        }
    }

    /**
     * Issues a book to a user with thread-safe lock synchronization.
     * Prevents race conditions where two threads try to issue the last remaining copy simultaneously.
     */
    public Transaction issueBook(String userId, String itemId) throws LibraryException {
        transactionLock.lock();
        try {
            User user = userCache.get(userId);
            if (user == null) {
                user = userDAO.findById(userId);
                if (user == null) throw new LibraryException("User not found: " + userId);
                userCache.put(userId, user);
            }

            LibraryItem item = itemCache.get(itemId);
            if (item == null) {
                item = bookDAO.findById(itemId);
                if (item == null) throw new LibraryException("Item not found: " + itemId);
                itemCache.put(itemId, item);
            }

            // Check borrow limits & availability
            if (!user.canBorrow()) {
                throw new UserLimitExceededException(userId, user.getMaxBorrowLimit());
            }

            if (!item.isAvailable()) {
                throw new BookNotAvailableException(itemId);
            }

            // Process issue (Polymorphic inventory updates)
            item.issueItem(userId);
            user.incrementBorrowedCount();

            // Create Transaction record
            String txId = "TX-" + System.currentTimeMillis();
            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(user.getBorrowDurationDays());

            Transaction tx = new Transaction(txId, userId, itemId, today, dueDate, null, 0.0, "ISSUED");

            // Persist changes to database
            bookDAO.update(item);
            userDAO.update(user);
            transactionDAO.save(tx);

            return tx;

        } catch (DatabaseException e) {
            throw new LibraryException("Database error during issue operation: " + e.getMessage(), e);
        } finally {
            transactionLock.unlock();
        }
    }

    /**
     * Returns a borrowed book and computes any applicable overdue fines polymorphically.
     */
    public double returnBook(String transactionId) throws LibraryException {
        transactionLock.lock();
        try {
            Transaction tx = transactionDAO.findById(transactionId);
            if (tx == null) {
                throw new LibraryException("Transaction record not found: " + transactionId);
            }

            if ("RETURNED".equalsIgnoreCase(tx.getStatus())) {
                throw new LibraryException("Transaction " + transactionId + " is already returned.");
            }

            LibraryItem item = itemCache.get(tx.getItemId());
            if (item == null) item = bookDAO.findById(tx.getItemId());

            User user = userCache.get(tx.getUserId());
            if (user == null) user = userDAO.findById(tx.getUserId());

            LocalDate today = LocalDate.now();
            tx.setReturnDate(today);
            tx.setStatus("RETURNED");

            // Calculate polymorphic fine
            double fine = 0.0;
            if (today.isAfter(tx.getDueDate())) {
                long overdueDays = ChronoUnit.DAYS.between(tx.getDueDate(), today);
                double rawFine = item != null ? item.calculateOverdueFine((int) overdueDays) : overdueDays * 1.0;
                fine = user != null ? user.calculateDiscountedFine(rawFine) : rawFine;
            }

            tx.setFineAmount(fine);

            if (item != null) {
                item.returnItem();
                bookDAO.update(item);
            }

            if (user != null) {
                user.decrementBorrowedCount();
                userDAO.update(user);
            }

            transactionDAO.update(tx);
            return fine;

        } catch (DatabaseException e) {
            throw new LibraryException("Database error during return operation: " + e.getMessage(), e);
        } finally {
            transactionLock.unlock();
        }
    }

    public List<LibraryItem> getAllItems() throws DatabaseException {
        return bookDAO.findAll();
    }

    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAll();
    }

    public List<Transaction> getAllTransactions() throws DatabaseException {
        return transactionDAO.findAll();
    }

    public BookDAO getBookDAO() { return bookDAO; }
    public UserDAO getUserDAO() { return userDAO; }
    public TransactionDAO getTransactionDAO() { return transactionDAO; }
}
