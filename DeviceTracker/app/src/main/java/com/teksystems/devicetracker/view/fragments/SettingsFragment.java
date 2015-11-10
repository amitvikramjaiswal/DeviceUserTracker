package com.teksystems.devicetracker.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SettingEntity;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;

/**
 * Created by akokala on 15-9-15.
 */
public class SettingsFragment extends BaseFragment implements TextWatcher, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = "SettingsFragment";
    private Context mContext;
    private ScrollView mScrollView;
    private MenuActivity mMenuActivity;
    private SettingEntity mSettingEntity;
    private String mSessionTimeValue, mAlertIntervalTimeValue, mWrongPasswordValue, mDefaultPasswordValue, mDefaultPasswordFromObject,
            mObjectIdFromObject;
    private int mSessionTimeValueInt, mAlertIntervalTimeValueInt, mWrongPasswordValueInt, mSessionTimeFromObject,
            mAlertIntervalTimeFromObject, mWrongPasswordFromObject;

    private EditText mSessionTime, mAlertIntervalTime, mWrongPassword, mDefaultPassword;
    private CheckBox mCheckBox;
    private Button mResetButton, mSaveButton;

    @Override
    int getFragmentLayoutResourceId() {
        return R.layout.fragment_settings;
    }

    @Override
    void findViews() {
        mSessionTime = (EditText) mRootView.findViewById(R.id.et_enter_session_time);
        mAlertIntervalTime = (EditText) mRootView.findViewById(R.id.et_enter_alert_time_interval);
        mWrongPassword = (EditText) mRootView.findViewById(R.id.et_enter_wrong_passwords);
        mDefaultPassword = (EditText) mRootView.findViewById(R.id.et_default_passwords);
        mResetButton = (Button) mRootView.findViewById(R.id.bt_reset_button);
        mSaveButton = (Button) mRootView.findViewById(R.id.bt_save_button);
        mCheckBox = (CheckBox) mRootView.findViewById(R.id.et_show_passwords);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onCreateView = super.onCreateView(inflater, container, savedInstanceState);

        if (getActivity() == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
         /* Initialize MenuActivity*/
        mMenuActivity = (MenuActivity) getActivity();
        mContext = mMenuActivity.getApplicationContext();
        mMenuActivity.getSupportActionBar().setTitle(R.string.navigation_item5);

        getSettingValueFromObject();

        addListener();

        return onCreateView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_reset_button:
                mMenuActivity.hideKeyBoard();
                resetSettingValues();

                /*Hide the Save and Reset Button After SuccessFull Reset */
                mSaveButton.setEnabled(false);
                mResetButton.setEnabled(false);
                break;

            case R.id.bt_save_button:
                mMenuActivity.hideKeyBoard();
                updateSettingTable();
                break;
            default:
                break;
        }
    }

    public void addListener() {
        mResetButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mCheckBox.setOnCheckedChangeListener(this);

         /*Add listener for editText*/
        mSessionTime.addTextChangedListener(this);
        mAlertIntervalTime.addTextChangedListener(this);
        mWrongPassword.addTextChangedListener(this);
        mDefaultPassword.addTextChangedListener(this);
    }

    /* Method to get the Values from DUCacheHelper Object and Pre populate in Settings Page,When Setting page launches*/
    public void getSettingValueFromObject() {
        mObjectIdFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getObjectId();
        mSessionTimeFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getSessionTimeout();
        mAlertIntervalTimeFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getAlertInterval();
        mWrongPasswordFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getMaxAttempts();
        mDefaultPasswordFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getDefaultPassword();

        mSessionTime.setText(mSessionTimeFromObject + "");
        mAlertIntervalTime.setText(mAlertIntervalTimeFromObject + "");
        mWrongPassword.setText(mWrongPasswordFromObject + "");
        mDefaultPassword.setText(mDefaultPasswordFromObject);
    }

    /*Method to Update Setting table*/
    public void updateSettingTable() {

        mSessionTimeValue = mSessionTime.getText().toString().trim();
        if (!mSessionTimeValue.isEmpty() && mSessionTimeValue != null) {
            mSessionTimeValueInt = Integer.parseInt(mSessionTimeValue);
        }

        mAlertIntervalTimeValue = mAlertIntervalTime.getText().toString().trim();
        if (!mAlertIntervalTimeValue.isEmpty() && mAlertIntervalTimeValue != null) {
            mAlertIntervalTimeValueInt = Integer.parseInt(mAlertIntervalTimeValue);
        }
        mWrongPasswordValue = mWrongPassword.getText().toString().trim();
        if (!mWrongPasswordValue.isEmpty() && mWrongPasswordValue != null) {
            mWrongPasswordValueInt = Integer.parseInt(mWrongPasswordValue);
        }
        mDefaultPasswordValue = mDefaultPassword.getText().toString().trim();

        SettingEntity settingEntity = new SettingEntity();
        settingEntity.setObjectId(DUCacheHelper.getDuCacheHelper().getSettingEntity().getObjectId());
        settingEntity.setAlertInterval(mAlertIntervalTimeValueInt);
        settingEntity.setSessionTimeout(mSessionTimeValueInt);
        settingEntity.setMaxAttempts(mWrongPasswordValueInt);
        settingEntity.setDefaultPassword(mDefaultPasswordValue);

        mMenuActivity.showProgressDialog(mMenuActivity.getString(R.string.progress_bar_message));
        mMenuActivity.getPresenter().saveSettingValues(settingEntity, new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.showAlertDialog(null, getString(R.string.setting_update_message), null, null, getString(R.string.ok_msg), null,
                            new AlertDialogHandler() {
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
                    updateSettingEntity();
                    mMenuActivity.hideProgressDialog();

                        /*Hide the Save and Reset Button After SuccessFull Update */
                    mSaveButton.setEnabled(false);
                    mResetButton.setEnabled(false);
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                } else if (parseException.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(mMenuActivity);
                } else {
                    Utils.showUnexpectedError(mMenuActivity);
                }
            }
        });

    }

    /* Method for Reset the Setting values*/
    public void resetSettingValues() {
        mSessionTime.setText(DUCacheHelper.getDuCacheHelper().getSettingEntity().getSessionTimeout() + "");
        mAlertIntervalTime.setText(DUCacheHelper.getDuCacheHelper().getSettingEntity().getAlertInterval() + "");
        mWrongPassword.setText(DUCacheHelper.getDuCacheHelper().getSettingEntity().getMaxAttempts() + "");
        mDefaultPassword.setText(DUCacheHelper.getDuCacheHelper().getSettingEntity().getDefaultPassword());
    }

    /*Method for Updating SettingEntity Table*/
    public void updateSettingEntity() {
        mSettingEntity = DUCacheHelper.getDuCacheHelper().getSettingEntity();
        mSettingEntity.setSessionTimeout(mSessionTimeValueInt);
        mSettingEntity.setAlertInterval(mAlertIntervalTimeValueInt);
        mSettingEntity.setMaxAttempts(mWrongPasswordValueInt);
        mSettingEntity.setDefaultPassword(mDefaultPasswordValue);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!mDefaultPassword.getText().toString().isEmpty()) {
            if (isChecked) {
                mDefaultPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                mDefaultPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String sessionTime = mSessionTime.getText().toString().trim();
        String alertInterval = mAlertIntervalTime.getText().toString().trim();
        String passwordAttempt = mWrongPassword.getText().toString().trim();
        String defaultPassword = mDefaultPassword.getText().toString().trim();

        mSessionTimeFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getSessionTimeout();
        mAlertIntervalTimeFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getAlertInterval();
        mWrongPasswordFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getMaxAttempts();
        mDefaultPasswordFromObject = DUCacheHelper.getDuCacheHelper().getSettingEntity().getDefaultPassword();

        boolean isNotNullOrNotEmpty = sessionTime != null && !sessionTime.equalsIgnoreCase("0") && !sessionTime.isEmpty() && alertInterval != null && !alertInterval.equalsIgnoreCase("0") && !alertInterval.isEmpty() && passwordAttempt != null && !passwordAttempt.equalsIgnoreCase("0") && !passwordAttempt.isEmpty() && defaultPassword != null && !defaultPassword.equalsIgnoreCase("") && !defaultPassword.isEmpty();
        boolean isValueChanged = !sessionTime.equalsIgnoreCase(String.valueOf(mSessionTimeFromObject)) || !alertInterval.equalsIgnoreCase(String.valueOf(mAlertIntervalTimeFromObject)) || !passwordAttempt.equalsIgnoreCase(String.valueOf(mWrongPasswordFromObject)) || !defaultPassword.equalsIgnoreCase(mDefaultPasswordFromObject);

        if (isNotNullOrNotEmpty) {
            if (isValueChanged) {
                mSaveButton.setEnabled(true);
            } else {
                mSaveButton.setEnabled(false);
            }
        } else {
            mSaveButton.setEnabled(false);
        }

        if (isValueChanged) {
            mResetButton.setEnabled(true);
        } else {
            mResetButton.setEnabled(false);
        }

        if (defaultPassword.length() < Utility.SETTING_MIN_DEFAULT_LENGTH) {
            mSaveButton.setEnabled(false);
        }
    }
}
