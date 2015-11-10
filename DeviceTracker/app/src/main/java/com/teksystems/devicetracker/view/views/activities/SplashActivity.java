package com.teksystems.devicetracker.view.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SettingEntity;
import com.teksystems.devicetracker.presenter.presenters.SplashPresenter;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.SplashView;

import java.util.Date;
import java.util.List;

/**
 * Created by akokala on 23-9-15.
 */
public class SplashActivity extends BaseActivity<SplashView, SplashPresenter> implements SplashView {
    private SharedPreferences mSharedPreferencesSettings, mSharedPreferencesFirstTimeLaunch;
    private SettingEntity mSettingEntity;
    private int mSessionTimeOut, mAlertTimeInterval, mWrongPassword;
    private String mDefaultPassword;
    private ParseException mParseException;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        String objectId = getObjectId();

        if (objectId != null) {
            ParseObject parseObject = new ParseObject(Utility.SESSIONS);
            parseObject.setObjectId(objectId);
            parseObject.put(Utility.LOGOUT_TIME, new Date());
            parseObject.put(Utility.LOGGED_IN, false);
            getPresenter().saveObject(parseObject, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null && e.getCode() == Utility.NO_NETWORK) {
//                        Utils.showNetworkError(SplashActivity.this);
                    } else if (e == null) {
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().remove(Utility.OBJECT_ID).commit();
                    } else if (e.getCode() == Utility.TIME_OUT) {
//                        Utils.showRequestTimeOutError(SplashActivity.this);
                    } else {
//                        Utils.showUnexpectedError(SplashActivity.this);
                    }
                }
            });
        }
        getSettingTableValue();
        finishOnUiThread();
    }

    @Override
    public void finishOnUiThread() {
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                // close this activity
                finish();
            }
        }, Utility.SPLASH_TIME_OUT);
    }

    /*Method to getSetting Table Values and store in DUCacheHelper Object*/
    public void getSettingTableValue() {
        if (isFirstTimeLaunched() && mParseException != null) {

             /*First time launch*/
            setSharedPreferenceValue();

            /*Set SharedPreference value to false after First launch*/
            mSharedPreferencesFirstTimeLaunch.edit().putBoolean(Utility.FIRST_TIME_LAUNCH, false).commit();
        } else {
            getPresenter().getSettingValues(Utility.SETTING, new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException parseException) {
                    mParseException = parseException;
                     /*Fetch Setting Data and store in settingEntity*/
                    if (parseException == null) {

                           /* Update SettingEntity Values*/
                        mSettingEntity = DUCacheHelper.getDuCacheHelper().getSettingEntity();
                        mSettingEntity.setObjectId(list.get(0).getObjectId());
                        mSettingEntity.setSessionTimeout(list.get(0).getInt(Utility.SESSION_TIMEOUT));
                        mSettingEntity.setAlertInterval(list.get(0).getInt(Utility.ALERT_INTERVAL));
                        mSettingEntity.setMaxAttempts(list.get(0).getInt(Utility.MAX_ATTEMPTS));
                        mSettingEntity.setDefaultPassword(list.get(0).getString(Utility.DEFAULT_PASSWORD));

                            /* Update SharedPreference Values*/
                        mSharedPreferencesSettings = getSharedPreferences(Utility.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferencesSettings.edit();
                        editor.putInt(Utility.SESSION_TIMEOUT, mSettingEntity.getSessionTimeout());
                        editor.putInt(Utility.ALERT_INTERVAL, mSettingEntity.getAlertInterval());
                        editor.putInt(Utility.MAX_ATTEMPTS, mSettingEntity.getMaxAttempts());
                        editor.putString(Utility.DEFAULT_PASSWORD, mSettingEntity.getDefaultPassword());
                        editor.commit();

                    } else {
                        addSettingsData();
//                        Utils.showUnexpectedError(SplashActivity.this);
                    }

                }
            });
        }
    }

    private void addSettingsData() {
        mSettingEntity = DUCacheHelper.getDuCacheHelper().getSettingEntity();
        mSharedPreferencesSettings = getSharedPreferences(Utility.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        if (mSharedPreferencesSettings != null) {
            mSessionTimeOut = mSharedPreferencesSettings.getInt(Utility.SESSION_TIMEOUT, 0);
            mAlertTimeInterval = mSharedPreferencesSettings.getInt(Utility.ALERT_INTERVAL, 0);
            mWrongPassword = mSharedPreferencesSettings.getInt(Utility.MAX_ATTEMPTS, 0);
            mDefaultPassword = mSharedPreferencesSettings.getString(Utility.DEFAULT_PASSWORD, "");

            /*Set To SettingEntity*/
            mSettingEntity.setSessionTimeout(mSessionTimeOut);
            mSettingEntity.setAlertInterval(mAlertTimeInterval);
            mSettingEntity.setMaxAttempts(mWrongPassword);
            mSettingEntity.setDefaultPassword(mDefaultPassword);
        }
    }

   /* Method to store  constant values for SettingEntity ,if First time app launches, there is any parse error,
    take these values and update every time from Settings Table */

    public void setSharedPreferenceValue() {
        mSettingEntity = DUCacheHelper.getDuCacheHelper().getSettingEntity();
        mSharedPreferencesSettings = getSharedPreferences(Utility.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        /* Set setting values in SharedPreference*/
        SharedPreferences.Editor editor = mSharedPreferencesSettings.edit();
        editor.putInt(Utility.SESSION_TIMEOUT, 3);
        editor.putInt(Utility.ALERT_INTERVAL, 3);
        editor.putInt(Utility.MAX_ATTEMPTS, 3);
        editor.putString(Utility.DEFAULT_PASSWORD, "password@123");
        editor.commit();

        /*Get the values from SharedPreferences*/
        if (mSharedPreferencesSettings != null) {
            mSessionTimeOut = mSharedPreferencesSettings.getInt(Utility.SESSION_TIMEOUT, 0);
            mAlertTimeInterval = mSharedPreferencesSettings.getInt(Utility.ALERT_INTERVAL, 0);
            mWrongPassword = mSharedPreferencesSettings.getInt(Utility.MAX_ATTEMPTS, 0);
            mDefaultPassword = mSharedPreferencesSettings.getString(Utility.DEFAULT_PASSWORD, "");

            /*Set To SettingEntity*/
            mSettingEntity.setSessionTimeout(mSessionTimeOut);
            mSettingEntity.setAlertInterval(mAlertTimeInterval);
            mSettingEntity.setMaxAttempts(mWrongPassword);
            mSettingEntity.setDefaultPassword(mDefaultPassword);
        }
    }

    private String getObjectId() {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreference.getString(Utility.OBJECT_ID, null);
    }

    /*Method to check weather app is launched first time or not*/
    public boolean isFirstTimeLaunched() {
        mSharedPreferencesFirstTimeLaunch = getSharedPreferences(Utility.FIRST_TIME_APP_LAUNCH_PREFERENCE, 0);
        return mSharedPreferencesFirstTimeLaunch.getBoolean(Utility.FIRST_TIME_LAUNCH, true);
    }
}
