package com.library.model;

/**
 * Abstract Base Class for Library Users.
 * Demonstrates Abstraction, Polymorphism, and Encapsulation.
 */
public abstract class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private int currentBorrowedCount;

    public User(String userId, String name, String email, String phone, int currentBorrowedCount) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.currentBorrowedCount = currentBorrowedCount;
    }

    // Abstract methods defining polymorphic user behaviors
    public abstract String getUserRole();
    public abstract int getMaxBorrowLimit();
    public abstract int getBorrowDurationDays();
    public abstract double calculateDiscountedFine(double baseFine);

    public boolean canBorrow() {
        return currentBorrowedCount < getMaxBorrowLimit();
    }

    public void incrementBorrowedCount() {
        this.currentBorrowedCount++;
    }

    public void decrementBorrowedCount() {
        if (this.currentBorrowedCount > 0) {
            this.currentBorrowedCount--;
        }
    }

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getCurrentBorrowedCount() { return currentBorrowedCount; }
    public void setCurrentBorrowedCount(int currentBorrowedCount) { this.currentBorrowedCount = currentBorrowedCount; }
}
