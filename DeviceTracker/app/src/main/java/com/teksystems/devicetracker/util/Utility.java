package com.teksystems.devicetracker.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Utility class for static and reusable variables.
 */
public final class Utility {
    // Database related constants.

    // Parse App Keys
    public static final String APPLICATION_ID = "sgY2xHA8ZQgipIO3DTJFHfq6GjeizBIhw3HJtmxa";
    public static final String CLIENT_KEY = "2Uvr8n487ulhRBkgicgrOt1rRjVqeGrH1kMGD9Vw";

    // table name.
    public static final String USER_TABLE = "DUUsers";
    public static final String DEVICE = "Device";
    public static final String LOGIN_ATTEMPT = "LoginAttempt";
    public static final String SESSIONS = "Sessions";
    public static final String SETTING = "Setting";
    public static final String PROJECTS = "Projects";

    // common
    public static final String OBJECT_ID = "objectId";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String IS_ENABLED = "isEnabled";

    //user table
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String IS_ADMIN = "isAdmin";

    // device table
    public static final String DEVICE_OS = "deviceOS";
    public static final String MAC_ADDRESS = "macAddress";
    public static final String DEVICE_NAME = "deviceName";
    public static final String TAGGED_USER = "taggedUser";
    public static final String ACCESSORIES = "accessories";
    public static final String NOTES = "notes";
    public static final String DEVICE_VERSION = "deviceVersion";
//    public static final String WITH_ADMIN = "withAdmin";
    public static final String LAST_ACTIVE_SESSION = "lastActiveSession";

    // login attempt table
    public static final String ATTEMPT = "attempt";

    // sessions table
    public static final String LOGIN_TIME = "loginTime";
    public static final String LOGOUT_TIME = "logoutTime";
    public static final String LOGGED_IN = "loggedIn";
    public static final String USER_POINTER = "user";
    public static final String DEVICE_POINTER = "device";

    // setting table
    public static final String DEFAULT_PASSWORD = "defaultPassword";
    public static final String SESSION_TIMEOUT = "sessionTimeout";
    public static final String ALERT_INTERVAL = "alertInterval";
    public static final String MAX_ATTEMPTS = "maxAttempts";

    // Projects table
    public static final String PROJECT_NAME = "projectName";
    public static final String PROJECT_DESCRIPTION = "projectDescription";
    public static final String TAGGED_PROJECT = "taggedProject";

    // Fragment & Activity Names
    public static final String SHOW_USERS = "Users";
    public static final String PROJECT = "Projects";
    public static final String SHOW_DEVICES = "Devices";
    public static final String DEVICE_USERS = "Device Users";
    public static final String ABOUT = "About";
    public static final String HELP = "Help";

    public static final int EDIT_RESULT_CODE = 500;

    public static final String INVALID_STATE = "Activity not found. Error can be Ignored";

    // Parse exception codes
    public static final int USERNAME_ALREADY_EXISTS = 202;
    public static final int OBJECT_NOT_FOUND = 101;
    public static final int NO_NETWORK = 999;
    public static int TIME_OUT = 100;

    public static final String LAST_ACCESSED_TIME_FORMAT = "MMM dd, yyyy h:mm aaa";
    public static final String LOCAL_TIMEZONE = "GMT+05:30";

    public static int SPLASH_TIME_OUT = 3000;

    public static final String SETTINGS_SHARED_PREFERENCE = "SETTINGS_SHARED_PREFERENCE";
    public static final String FIRST_TIME_APP_LAUNCH_PREFERENCE = "FIRST_TIME_APP_LAUNCH_PREFERENCE";
    public static final String FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH";
    public static final int SETTING_MIN_DEFAULT_LENGTH = 6;

    //Modes
    public static final String EDIT_MODE = "EDIT";
    public static final String SAVE_MODE = "SAVE";
    public static final String ADMIN_MODE = "ADMIN_MODE";

    //OS Types
    public static final String ANDROID_OS = "Android";
    public static final String IOS_OS = "iOS";

    public static final String ALERT_TYPE = "ALERT_TYPE";
    public static final int RECURRING_ALERT = 0;
    public static final int SESSION_TIMEOUT_ALERT = 1;

    public static final long SESSION_TIMEOUT_UPPER_LIMIT = 2 * 60 * 1000;
    public static final long SESSION_TIMEOUT_LOWER_LIMIT = 2 * 1000;

    /**
     * Utility Constructor.
     */

    private Utility() {
    }

}
