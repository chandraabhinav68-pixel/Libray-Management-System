package com.library.interfaces;

import com.library.exceptions.LibraryException;

/**
 * Interface defining contract for items that can be issued and returned.
 * Demonstrates Interface concept in Java OOP.
 */
public interface Borrowable {
    boolean isAvailable();
    void issueItem(String userId) throws LibraryException;
    void returnItem() throws LibraryException;
    int getAvailableCopies();
}
