package com.teksystems.devicetracker.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.application.DeviceTrackerApplication;
import com.teksystems.devicetracker.component.LoggedInCheckService;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.log.Logger;
import com.teksystems.devicetracker.view.views.activities.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

/**
 * Utility methods.
 */
public final class Utils {

    private static final String LOG_TAG = "Utils";
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    private Utils() {
    }

    /**
     * Returns true if the string has a value
     */
    public static boolean hasContent(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * Creates an MD5 hash for the specified string.
     *
     * @param s The string
     * @return The MD5 hash
     */
    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Logger.e(LOG_TAG, e);
        }
        return "";
    }

    //Method to get the connectivity Status
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    //Method to get the Connected Wifi name
    public static String getWifiName(Context context) {

        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

    public static void showNetworkError(final BaseActivity activity) {
        activity.showAlertDialog(null, activity.getString(R.string.network_unavailable), null, null, activity.getString(R.string.ok_msg), null, new AlertDialogHandler() {

            @Override
            public void onPositiveButtonClicked() {
                activity.hideProgressDialog();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    public static void showRequestTimeOutError(final BaseActivity activity) {
        activity.showAlertDialog(null, activity.getString(R.string.no_network), null, null, activity.getString(R.string.ok_msg), null, new AlertDialogHandler() {

            @Override
            public void onPositiveButtonClicked() {
                activity.hideProgressDialog();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    public static void showUnexpectedError(final BaseActivity activity) {
        activity.showAlertDialog(null, activity.getString(R.string.unexpected_error), null, null, activity.getString(R.string.ok_msg), null, new AlertDialogHandler() {

            @Override
            public void onPositiveButtonClicked() {

            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
        activity.hideProgressDialog();
    }

    //Method to check the Internet Connectivity of the device
    public static boolean isNetworkAvailable(Context ctx) {
        if (LoggedInCheckService.scheduledTime != 0) {
            long remainingTime = LoggedInCheckService.scheduledTime - System.currentTimeMillis();

            if (remainingTime <= Utility.SESSION_TIMEOUT_UPPER_LIMIT) {
                if (remainingTime < Utility.SESSION_TIMEOUT_LOWER_LIMIT) {
                    try {
                        Thread.sleep(4000);
                        return false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LoggedInCheckService.stopSessionTimer();
                LoggedInCheckService.startSessionTimer(Utility.SESSION_TIMEOUT_UPPER_LIMIT);
            }
        }
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    //Method to fetch device MAC Address.
    public static String retrieveMacAddress(Context ctx) {
        WifiManager wimanager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }

    public static String getLocalizedLastAccessTime(SessionsEntity sessionsEntity) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utility.LAST_ACCESSED_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utility.LOCAL_TIMEZONE));

        Date lastAccessedTime = sessionsEntity.getLogoutTime() != null ? sessionsEntity.getLogoutTime() : sessionsEntity.getLoginTime();

        return lastAccessedTime != null ? simpleDateFormat.format(lastAccessedTime) : "";
    }

    public static void clearUserData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DeviceTrackerApplication.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Utility.OBJECT_ID);
        editor.commit();
        DUCacheHelper.getDuCacheHelper().setSessionsEntity(null);
    }


    public static boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}
