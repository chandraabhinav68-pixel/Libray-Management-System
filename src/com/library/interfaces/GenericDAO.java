package com.library.interfaces;

import com.library.exceptions.DatabaseException;
import java.util.List;

/**
 * Generic Data Access Object (DAO) Interface.
 * Demonstrates Generics concept (T, ID) and Interface contract for database CRUD operations.
 *
 * @param <T>  The entity type
 * @param <ID> The primary key / unique identifier type
 */
public interface GenericDAO<T, ID> {
    void save(T entity) throws DatabaseException;
    T findById(ID id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
    void update(T entity) throws DatabaseException;
    void delete(ID id) throws DatabaseException;
}
