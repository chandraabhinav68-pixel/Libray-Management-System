package com.library.model;

/**
 * Concrete Faculty User implementation.
 * Limit: 10 books, Borrow Duration: 30 days, Fine Discount: 50%
 */
public class FacultyUser extends User {
    private String department;

    public FacultyUser(String userId, String name, String email, String phone, int currentBorrowedCount, String department) {
        super(userId, name, email, phone, currentBorrowedCount);
        this.department = department;
    }

    @Override
    public String getUserRole() {
        return "FACULTY";
    }

    @Override
    public int getMaxBorrowLimit() {
        return 10;
    }

    @Override
    public int getBorrowDurationDays() {
        return 30;
    }

    @Override
    public double calculateDiscountedFine(double baseFine) {
        return baseFine * 0.50; // 50% discount for faculty members
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
