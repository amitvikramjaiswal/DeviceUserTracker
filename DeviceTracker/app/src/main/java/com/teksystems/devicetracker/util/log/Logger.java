package com.teksystems.devicetracker.util.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The logger interface allows logging from anywhere in the application through
 * a static interface. Note that {@link #addLogger(com.aerotek.android.app.util.log.Logger)} must be called
 * before logging has any effect.
 */
public abstract class Logger {

    private static final List<Logger> LOGGERS = Collections.synchronizedList(new ArrayList<Logger>());
    private LogLevel mLogLevel = LogLevel.ASSERT;

    public enum LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT;

        public boolean filterAllows(LogLevel other) {
            return other.compareTo(this) >= 0;
        }
    }

    /**
     * Add a logger to the list of active loggers.
     * 
     * @param logger
     *            The logger to add.
     */
    public static void addLogger(Logger logger) {
        synchronized (LOGGERS) {
            if (!LOGGERS.contains(logger)) {
                LOGGERS.add(logger);
            }
        }
    }

    /**
     * Remove a logger from the list of active loggers.
     * 
     * @param logger
     *            The logger to remove.
     */
    public static void removeLogger(Logger logger) {
        LOGGERS.remove(logger);
    }

    /**
     * Remove all the loggers.
     */
    public static void removeAllLoggers() {
        LOGGERS.clear();
    }

    /**
     * Gets a list of active loggers.
     */
    public static List<Logger> getLoggers() {
        synchronized (LOGGERS) {
            ArrayList<Logger> loggers = new ArrayList<Logger>();
            loggers.addAll(LOGGERS);
            return loggers;
        }
    }

    /**
     * Gets a list of active loggers of the specified class.
     * 
     * @param clazz
     *            The class.
     * @return A list of active loggers of this class, or an empty list if none
     *         are found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Logger> List<T> getLoggersByClass(Class<T> clazz) {
        synchronized (LOGGERS) {
            List<T> ret = new ArrayList<T>();
            for (Logger logger : LOGGERS) {
                if (logger.getClass().equals(clazz)) {
                    ret.add((T) logger);
                }
            }
            return ret;
        }
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#VERBOSE} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void v(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.VERBOSE, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#VERBOSE} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void v(String tag, Throwable th) {
        logToAllLoggers(LogLevel.VERBOSE, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#VERBOSE} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void v(String tag, String message) {
        logToAllLoggers(LogLevel.VERBOSE, tag, message, null);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#DEBUG} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void d(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.DEBUG, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#DEBUG} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void d(String tag, Throwable th) {
        logToAllLoggers(LogLevel.DEBUG, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#DEBUG} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void d(String tag, String message) {
        logToAllLoggers(LogLevel.DEBUG, tag, message, null);
    }
    
    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#WARN} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void w(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.WARN, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#WARN} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void w(String tag, Throwable th) {
        logToAllLoggers(LogLevel.WARN, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#WARN} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void w(String tag, String message) {
        logToAllLoggers(LogLevel.WARN, tag, message, null);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ERROR} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void e(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.ERROR, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ERROR} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void e(String tag, Throwable th) {
        logToAllLoggers(LogLevel.ERROR, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ERROR} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void e(String tag, String message) {
        logToAllLoggers(LogLevel.ERROR, tag, message, null);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#INFO} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void i(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.INFO, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#INFO} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void i(String tag, Throwable th) {
        logToAllLoggers(LogLevel.INFO, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#INFO} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void i(String tag, String message) {
        logToAllLoggers(LogLevel.INFO, tag, message, null);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ASSERT} message and exception.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     * @param th
     *            The exception (optional).
     */
    public static void a(String tag, String message, Throwable th) {
        logToAllLoggers(LogLevel.ASSERT, tag, message, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ASSERT} exception.
     * 
     * @param tag
     *            The log tag.
     * @param th
     *            The exception (optional).
     */
    public static void a(String tag, Throwable th) {
        logToAllLoggers(LogLevel.ASSERT, tag, null, th);
    }

    /**
     * Log a {@link com.aerotek.android.app.util.log.Logger.LogLevel#ASSERT} message.
     * 
     * @param tag
     *            The log tag.
     * @param message
     *            The log message (optional).
     */
    public static void a(String tag, String message) {
        logToAllLoggers(LogLevel.ASSERT, tag, message, null);
    }

    /**
     * Log a message to all loggers.
     * 
     * @param level
     *            The log level.
     * @param tag
     *            The log tag.
     * @param message
     *            The log message. May be null.
     * @param th
     *            The exception that caused the message. May be null.
     */
    static void logToAllLoggers(LogLevel level, String tag, String message, Throwable th) {
        synchronized (LOGGERS) {
            for (Logger logger : LOGGERS) {
                if (logger.getLogLevel().filterAllows(level)) {
                    logger.log(level, tag, message, th);
                }
            }
        }
    }

    /**
     * Sets the log level.
     * 
     * @param logLevel
     *            The log level to set.
     */
    public void setLogLevel(LogLevel logLevel) {
        mLogLevel = logLevel;
    }

    /**
     * Gets the log level.
     * 
     * @return The log level.
     */
    public LogLevel getLogLevel() {
        return mLogLevel;
    }

    /**
     * Callback to implement in the logger when a message must be logged.
     * 
     * @param level
     *            The log level.
     * @param tag
     *            The log tag.
     * @param message
     *            The log message.
     * @param stacktrace
     *            A throwable (if applicable)
     */
    abstract void log(LogLevel level, String tag, String message, Throwable th);
}
