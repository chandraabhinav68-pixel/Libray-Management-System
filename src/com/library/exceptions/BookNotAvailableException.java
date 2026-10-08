package com.library.exceptions;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String itemId) {
        super("Library item with ID '" + itemId + "' is currently out of stock or unavailable.");
    }
}
