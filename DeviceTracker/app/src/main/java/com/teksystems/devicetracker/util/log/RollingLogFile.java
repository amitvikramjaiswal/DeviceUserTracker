package com.teksystems.devicetracker.util.log;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Manages access to a set of log files. The log is bounded by a maximum size,
 * and is split into chunks e.g. file, file.1, file.2, etc. The most recent file
 * is the file with the smallest number.
 */
public class RollingLogFile {
    private static final int MEGABYTE = 1024 * 1024;
    private long mMaxLogSizeBytes;
    private int mNumSegments = 10;
    private String mBaseFileName;
    private FileOutputStream mCurrentFileStream;
    private static final String LOG_TAG = "RollingLogFile";
    private boolean mFailedOpen;

    public RollingLogFile(String baseFileName) {
        mBaseFileName = baseFileName;
    }

    /**
     * Opens the log file(s) for writing.
     */
    public void open() {
        if (mFailedOpen) {
            return;
        }

        File base = new File(mBaseFileName);
        if (mMaxLogSizeBytes > 0 && base.length() > (mMaxLogSizeBytes / mNumSegments - 1024)) {
            Log.d(LOG_TAG, "Log file exceeds maximum size");
            close();
            renameLogs();
            open();
        } else if (mCurrentFileStream == null) {
            try {
                mCurrentFileStream = new FileOutputStream(mBaseFileName, true);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Could not open log file " + e.getMessage());
                mFailedOpen = true;
                mCurrentFileStream = null;
            }
        }
    }

    private String getFileNameForIndex(int i) {
        return mBaseFileName + (i > 0 ? "." + String.valueOf(i) : "");
    }

    private void renameLogs() {
        // First delete the oldest log
        File oldest = new File(getFileNameForIndex(mNumSegments - 1));
        oldest.delete();

        // Chain rename the rest
        for (int i = mNumSegments - 2; i >= 0; i--) {
            File chunk = new File(getFileNameForIndex(i));
            chunk.renameTo(new File(getFileNameForIndex(i + 1)));
        }
    }

    /**
     * Deletes the log file(s).
     */
    public void delete() {
        Log.d(LOG_TAG, "delete()");
        for (int i = 0; i < mNumSegments; i++) {
            File chunk = new File(getFileNameForIndex(i));
            if (chunk.delete()) {
                Log.d(LOG_TAG, "Deleted file " + chunk.getAbsolutePath());
            }
        }
        new File(mBaseFileName + ".zip").delete();
    }

    /**
     * Closes the log file(s).
     */
    public void close() {
        Log.d(LOG_TAG, "close()");
        if (mCurrentFileStream != null) {
            try {
                mCurrentFileStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing file", e);
            }
        }
        mFailedOpen = false;
        mCurrentFileStream = null;
    }

    /**
     * Logs a text message.
     */
    public void log(String text) {
        open();
        if (mCurrentFileStream != null) {
            try {
                mCurrentFileStream.write((text + "\r\n").getBytes());
                mCurrentFileStream.flush();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed writing log message", e);
                close();
            }
        }
    }

    /**
     * Zips the files and returns a path to the zip.
     */
    public File getZip() {
        try {
            close(); // if the file is currently open close it
            Log.d(LOG_TAG, "getZip()");
            File zipFile = new File(mBaseFileName + ".zip");
            zipFile.delete();
            ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < mNumSegments; i++) {
                File chunk = new File(getFileNameForIndex(i));
                if (chunk.exists()) {
                    FileInputStream fis = new FileInputStream(chunk);
                    writeToZipFile(zipStream, fis, chunk.getName());
                }
            }
            zipStream.close();
            return zipFile;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to create zip file", e);
        }

        return null;
    }

    void writeToZipFile(ZipOutputStream zout, InputStream is, String fileName) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[4096];
            ZipEntry ze = new ZipEntry(fileName);
            zout.putNextEntry(ze);
            int count;
            while ((count = bis.read(buffer, 0, 4096)) != -1) {
                zout.write(buffer, 0, count);
            }
            zout.closeEntry();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to write to zip file", e);
        }
    }

    public void setMaximumSize(long size) {
        mMaxLogSizeBytes = MEGABYTE * size;
    }

    public long getMaximumSize() {
        return mMaxLogSizeBytes / MEGABYTE;
    }
}
