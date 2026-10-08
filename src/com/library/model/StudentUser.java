package com.library.model;

/**
 * Concrete Student User implementation.
 * Limit: 3 books, Borrow Duration: 14 days, Fine Discount: 0%
 */
public class StudentUser extends User {
    private String studentIdNumber;

    public StudentUser(String userId, String name, String email, String phone, int currentBorrowedCount, String studentIdNumber) {
        super(userId, name, email, phone, currentBorrowedCount);
        this.studentIdNumber = studentIdNumber;
    }

    @Override
    public String getUserRole() {
        return "STUDENT";
    }

    @Override
    public int getMaxBorrowLimit() {
        return 3;
    }

    @Override
    public int getBorrowDurationDays() {
        return 14;
    }

    @Override
    public double calculateDiscountedFine(double baseFine) {
        return baseFine; // No discount for standard students
    }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }
}
