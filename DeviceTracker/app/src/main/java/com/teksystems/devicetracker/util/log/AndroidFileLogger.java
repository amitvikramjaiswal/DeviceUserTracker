package com.teksystems.devicetracker.util.log;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.inject.Inject;
import com.teksystems.devicetracker.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages logging to a file.
 */
public class AndroidFileLogger extends Logger {
    private static final String LOG_TAG = "FileLogger";
    private final Application mApplication;
    private RollingLogFile mRollingLogFile;
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DEBUG_LOG_FILE_NAME = "debug_log.txt";
    private SimpleDateFormat mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    /**
     * Create an Android logger.
     * 
     * @param application
     *            The Application object.
     */
    @Inject
    public AndroidFileLogger(Application application) {
        mApplication = application;
        mRollingLogFile = new RollingLogFile(new File(mApplication.getExternalFilesDir(null), DEBUG_LOG_FILE_NAME).getAbsolutePath());
        Log.v(LOG_TAG, "Initialized file logger");
    }

    /**
     * Sets the output date format.
     * 
     * @param format
     *            The output date format.
     */
    public void setDateFormatString(String format) {
        mDateFormat = new SimpleDateFormat(format);
        Logger.d(LOG_TAG, "mDateFormat set to " + mDateFormat);
    }

    /**
     * Gets the output date format.
     * 
     * @return The output date format.
     */
    public SimpleDateFormat getDateFormat() {
        return mDateFormat;
    }

    /**
     * Sets the log file size.
     * 
     * @param size
     *            The maximum log file size in megabytes.
     */
    public void setLogFileSize(long size) {
        mRollingLogFile.setMaximumSize(size);
    }

    /**
     * Gets the log file size.
     */
    public long getLogFileSize() {
        return mRollingLogFile.getMaximumSize();
    }

    /**
     * Emails the log file.
     */
    public void emailLogs() {
        Logger.d(LOG_TAG, "emailLogs");
        try {
            File zip = mRollingLogFile.getZip();
            if (zip != null) {

                String appName = "Unknown";
                String appVersion = "Unknown";
                try {
                    appName = mApplication.getPackageManager()
                            .getApplicationLabel(mApplication.getPackageManager().getApplicationInfo(mApplication.getPackageName(), 0))
                            .toString();
                    appVersion = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0).versionName;
                } catch (Exception e) {
                    Logger.e(LOG_TAG, "Could not get name and version", e);
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, appName + " - " + appVersion);
                intent.setType("application/zip");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zip));

                mApplication.startActivity(Intent.createChooser(intent, "Send logs").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                Logger.e(LOG_TAG, "zip file was null");
            }
        } catch (Exception e) {
            Logger.e(LOG_TAG, "Failed sending logs", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void log(LogLevel level, String tag, String message, Throwable th) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(mDateFormat.format(new Date()));
            sb.append(':');
            sb.append(level.toString());
            sb.append(':');
            sb.append(tag);
            sb.append(':');
            if (message != null) {
                sb.append(message);
            }
            String stacktrace = Log.getStackTraceString(th);
            if (Utils.hasContent(stacktrace)) {
                sb.append("\r\n");
                sb.append(stacktrace);
            }

            mRollingLogFile.log(sb.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to log to file", e);
        }
    }
}
