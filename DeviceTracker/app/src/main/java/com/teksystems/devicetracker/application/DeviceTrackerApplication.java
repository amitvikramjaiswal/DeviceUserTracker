package com.teksystems.devicetracker.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.inject.AbstractModule;
import com.parse.Parse;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.log.Logger;

/**
 * Created by akokala on 2-9-15.
 */
public class DeviceTrackerApplication extends MultiDexApplication {
    private static final String LOG_TAG = "Application";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        /*Initialize the Parse*/
        Parse.initialize(this, Utility.APPLICATION_ID, Utility.CLIENT_KEY);

//        Fabric.with(this, new Crashlytics());
        DeviceTrackerApplication.context = getApplicationContext();

        // set uncaught exception handler
        final Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                android.util.Log.e("UNCAUGHT EXCEPTION", thread.toString());
                Logger.d(LOG_TAG, "Exception", ex);
                Logger.e(LOG_TAG, thread.toString(), ex);
                // chain this so the app ends correctly
                handler.uncaughtException(thread, ex);
            }
        });
    }

    /**
     * Add bindings in configure() to manually configure RoboGuice.
     */
    public static class BindingsModule extends AbstractModule {
        /**
         * {@inheritDoc}
         */
        @Override
        protected void configure() {
            return;
        }
    }
}
