package com.library.exceptions;

/**
 * Custom base exception class for Library System domain logic.
 * Demonstrates Exception Handling in Java OOP.
 */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
