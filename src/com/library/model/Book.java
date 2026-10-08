package com.library.model;

/**
 * Physical Book item inheriting from LibraryItem.
 * Demonstrates Inheritance and Method Overriding (Polymorphism).
 */
public class Book extends LibraryItem {
    private String isbn;
    private int pages;

    public Book(String itemId, String title, String author, String category, int totalCopies, int availableCopies, String isbn, int pages) {
        super(itemId, title, author, category, totalCopies, availableCopies);
        this.isbn = isbn;
        this.pages = pages;
    }

    @Override
    public String getItemType() {
        return "PHYSICAL_BOOK";
    }

    @Override
    public double calculateOverdueFine(int daysOverdue) {
        // $1.50 per overdue day for physical books
        return daysOverdue <= 0 ? 0.0 : daysOverdue * 1.50;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}
