package com.library.model;

import com.library.interfaces.Borrowable;
import com.library.interfaces.Searchable;
import com.library.exceptions.LibraryException;
import com.library.exceptions.BookNotAvailableException;

/**
 * Abstract Base Class for all Library Inventory Items.
 * Demonstrates Abstraction and Encapsulation in OOP.
 */
public abstract class LibraryItem implements Borrowable, Searchable {
    private String itemId;
    private String title;
    private String author;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public LibraryItem(String itemId, String title, String author, String category, int totalCopies, int availableCopies) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Abstract method for polymorphism
    public abstract String getItemType();
    public abstract double calculateOverdueFine(int daysOverdue);

    @Override
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public void issueItem(String userId) throws LibraryException {
        if (!isAvailable()) {
            throw new BookNotAvailableException(itemId);
        }
        this.availableCopies--;
    }

    @Override
    public void returnItem() throws LibraryException {
        if (availableCopies < totalCopies) {
            this.availableCopies++;
        }
    }

    @Override
    public boolean matchesSearch(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String q = query.toLowerCase();
        return itemId.toLowerCase().contains(q) ||
               title.toLowerCase().contains(q) ||
               author.toLowerCase().contains(q) ||
               category.toLowerCase().contains(q);
    }

    // Getters and Setters (Encapsulation)
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    @Override
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}
