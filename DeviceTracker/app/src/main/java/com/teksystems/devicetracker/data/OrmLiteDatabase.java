package com.teksystems.devicetracker.data;


import com.google.inject.ImplementedBy;
import com.j256.ormlite.dao.Dao;

/**
 * Interface for the ORMLite database implementation.
 */
@ImplementedBy(OrmLiteSqliteDatabase.class)
public interface OrmLiteDatabase {

    /**
     * Called when the database is first created.
     */
    void onCreate();

    /**
     * Called when the database is upgraded.
     *
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    void onUpgrade(int oldVersion, int newVersion);

    /**
     * Create a new table for an object class if it does not exist.
     *
     * @param clazz The class for which to create a table.
     */
    void createTableIfNotExists(Class<?> clazz);

    /**
     * Get the ORMlite internal DAO for this class.
     *
     * @param clazz The class.
     * @return The DAO.
     */
    <T> Dao<T, Integer> getDao(Class<T> clazz);
}

