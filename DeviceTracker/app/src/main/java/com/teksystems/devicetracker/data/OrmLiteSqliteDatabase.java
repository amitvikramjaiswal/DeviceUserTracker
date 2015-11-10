package com.teksystems.devicetracker.data;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.teksystems.devicetracker.util.log.Logger;

import java.sql.SQLException;

/**
 * Implementation of the ORM lite database for Android SQLite.
 */
@Singleton
public class OrmLiteSqliteDatabase implements OrmLiteDatabase {

    private OrmLiteSqliteOpenHelperExt mHelper;
    private static final int DATABASE_VERSION = 1;
    private static final String LOG_TAG = "OrmLiteSqliteDatabase";

    @Inject
    void setApplication(Application application) {
        mHelper = new OrmLiteSqliteOpenHelperExt(application, "database.db", null, DATABASE_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        Logger.d(LOG_TAG, "onCreate");
        // No table pre-creations. The DAOs are responsible to create the
        // tables.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(int oldVersion, int newVersion) {
        Logger.d(LOG_TAG, "onUpgrade");
        // Nothing to do (for now).
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTableIfNotExists(Class<?> clazz) {
        try {
            TableUtils.createTableIfNotExists(mHelper.getConnectionSource(), clazz);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to create table", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Dao<T, Integer> getDao(Class<T> clazz) {
        try {
            return mHelper.getDao(clazz);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to create dao", e);
        }
    }

    /**
     * This subclass is the interface with the Android SQLite database; it has
     * callbacks for creation and database ugprade.
     */
    private class OrmLiteSqliteOpenHelperExt extends OrmLiteSqliteOpenHelper {
        OrmLiteSqliteOpenHelperExt(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
            super(context, databaseName, factory, databaseVersion);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
            OrmLiteSqliteDatabase.this.onCreate();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
            OrmLiteSqliteDatabase.this.onUpgrade(oldVersion, newVersion);
        }
    }
}