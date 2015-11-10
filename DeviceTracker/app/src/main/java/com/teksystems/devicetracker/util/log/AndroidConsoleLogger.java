package com.teksystems.devicetracker.util.log;

import android.util.Log;

import com.teksystems.devicetracker.util.Utils;

/**
 * Implementation for logging to the Android debug console.
 */
public class AndroidConsoleLogger extends Logger {
    private static final String LOG_TAG = "ConsoleLogger";

    /**
     * Create an Android logger.
     */
    public AndroidConsoleLogger() {
        Log.v(LOG_TAG, "Initialized console logger");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void log(LogLevel level, String tag, String message, Throwable th) {
        StringBuffer sb = new StringBuffer(message != null ? message : "");
        String stacktrace = Log.getStackTraceString(th);
        if (Utils.hasContent(stacktrace)) {
            sb.append('\n');
            sb.append(stacktrace);
        }
        Log.println(getAndroidLevel(level), tag, sb.toString());
    }

    int getAndroidLevel(LogLevel level) {
        switch (level) {
        case VERBOSE:
            return Log.VERBOSE;
        case DEBUG:
            return Log.DEBUG;
        case INFO:
            return Log.INFO;
        case WARN:
            return Log.WARN;
        case ERROR:
            return Log.ERROR;
        case ASSERT:
            return Log.ASSERT;
        default:
            throw new IllegalArgumentException("Unknown android level for level=" + level);
        }
    }
}
