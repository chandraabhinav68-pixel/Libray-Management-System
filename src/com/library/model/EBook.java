package com.library.model;

/**
 * Digital E-Book inheriting from LibraryItem.
 * Demonstrates Polymorphism with custom fine rules for digital assets.
 */
public class EBook extends LibraryItem {
    private String downloadUrl;
    private double fileSizeMb;

    public EBook(String itemId, String title, String author, String category, int totalCopies, int availableCopies, String downloadUrl, double fileSizeMb) {
        super(itemId, title, author, category, totalCopies, availableCopies);
        this.downloadUrl = downloadUrl;
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    public String getItemType() {
        return "EBOOK";
    }

    @Override
    public double calculateOverdueFine(int daysOverdue) {
        // Reduced fine ($0.50/day) for digital books
        return daysOverdue <= 0 ? 0.0 : daysOverdue * 0.50;
    }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public double getFileSizeMb() { return fileSizeMb; }
    public void setFileSizeMb(double fileSizeMb) { this.fileSizeMb = fileSizeMb; }
}
