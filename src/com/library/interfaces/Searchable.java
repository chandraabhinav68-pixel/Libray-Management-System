package com.library.interfaces;

/**
 * Interface for search functionality across library entities.
 * Demonstrates abstraction and interface design.
 */
public interface Searchable {
    boolean matchesSearch(String query);
}
