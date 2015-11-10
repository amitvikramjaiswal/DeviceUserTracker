package com.teksystems.devicetracker.view.views.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.application.DeviceTrackerApplication;
import com.teksystems.devicetracker.component.LoggedInCheckService;
import com.teksystems.devicetracker.data.entities.LoginAttemptEntity;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.presenter.presenters.LoginPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.LoginView;
import com.teksystems.devicetracker.view.views.WelcomeUserView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import roboguice.inject.InjectView;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView, WelcomeUserView, GetCallback<ParseObject>, View.OnFocusChangeListener, View.OnClickListener {
    private static final Pattern USERNAME_PATTERN = Pattern
            .compile("[a-zA-Z]{1,250}");
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("[a-zA-Z0-9+_.]{4,16}");
    private static final int LOGIN_REQUEST = 0;
    private static final int INCREMENT_ATTEMPT_REQUEST = 1;
    private static final int FETCH_DEVICE_FOR_MAC_ADDRESS_REQUEST = 2;
    private int requestCode;
    @InjectView(R.id.sv_login_activity)
    private ScrollView svLogin;
    @InjectView(R.id.et_username)
    private EditText mUsername;
    @InjectView(R.id.pb_login_progress)
    private View mLoginStatusView;
    @InjectView(R.id.tv_mac_address)
    private TextView mMacAddress;
    @InjectView(R.id.et_password)
    private TextView mPassword;
    @InjectView(R.id.id_login_button)
    private Button btnLogin;
    @InjectView(R.id.cb_keep_signed_in)
    private CheckBox saveLoginSwitch;
    @InjectView(R.id.tv_about_us)
    private TextView tvAboutUs;
    @InjectView(R.id.tv_help)
    private TextView tvHelp;
    private SharedPreferences loginPreferences;
    private int mMaxAttempt;
    private LoginAttemptEntity loginAttemptEntity;
    private String errorMsg = null;
    private UserEntity userEntity;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        addListeners();
        //  added below 2 lines TO  READ  SAVED  username  NEXT-TIME OPENING Application
        loginPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        mUsername.setText(loginPreferences.getString("username", ""));

        Intent intent = new Intent(this, LoggedInCheckService.class);
        intent.putExtra(Utility.ALERT_TYPE, Utility.RECURRING_ALERT);
        startService(intent);
    }

    private void onLoginButtonClick() {
        mMacAddress.setText("");
        final String username = mUsername.getText().toString().toLowerCase().trim();
        final String password = mPassword.getText().toString().trim();
        if (username.equals("") || password.equals("")) {
            showAlertCredentials();
            mUsername.requestFocus();
        } else {
            showProgress(true);
            btnLogin.setText(getString(R.string.btn_text_logging));
            if (saveLoginSwitch.isChecked()) {
                loginPreferences.edit().putString("username", username).commit();
            }
            login(username);
        }
    }

    //login happening when network available if not available showing alert.
    public void login(String username) {
        String password = mPassword.getText().toString();
        requestCode = LOGIN_REQUEST;
        getPresenter().customLogin(username, password, LoginActivity.this);
        hideKeyBoard();
        mUsername.clearFocus();
        mPassword.clearFocus();
    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {
        switch (requestCode) {
            case LOGIN_REQUEST:
                onLoginRequestDone(parseObject, e);
                break;
            case FETCH_DEVICE_FOR_MAC_ADDRESS_REQUEST:
                onFetchDeviceDone(parseObject, e);
                break;
            case INCREMENT_ATTEMPT_REQUEST:
                onIncrementAttemptRequest(parseObject, e);
                break;
        }
    }

    private void onIncrementAttemptRequest(ParseObject parseObject, ParseException e) {
        if (parseObject != null) {
            mMaxAttempt = DUCacheHelper.getDuCacheHelper().getSettingEntity().getMaxAttempts();
            //verifying loginattempt and maximum attempt from DU cache helper and then performing login else showing alert.
            if (parseObject.getInt(Utility.ATTEMPT) < mMaxAttempt) {
                parseObject.put(Utility.ATTEMPT, parseObject.getInt(Utility.ATTEMPT) + 1);
                getPresenter().saveObject(parseObject, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });
                showAlertLoginFailure(errorMsg);
            } else {
                showMaximumAttemptsFailure();
            }
            btnLogin.setText(getString(R.string.activity_login_sign_in));
            hideProgressDialog();
        } else if (e != null && e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else {
            showAlertLoginFailure(errorMsg);
        }
    }

    private void onFetchDeviceDone(ParseObject parseObject, ParseException e) {
        if (parseObject != null) {
            if (parseObject.getBoolean(Utility.IS_ENABLED)) {
                addToSessionsAfterSuccessfulLogin(parseObject);
            } else {
                deviceInactive(parseObject.getString(Utility.DEVICE_NAME));
                hideProgressDialog();
                btnLogin.setText(getString(R.string.activity_login_sign_in));
            }
        } else if (e != null && e.getCode() != Utility.TIME_OUT && e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else if (e != null && userEntity.isAdmin()) {
            deviceNotRegistered(true);
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else {
            deviceNotRegistered(false);
            mMacAddress.setText("ID: " + Utils.retrieveMacAddress(LoginActivity.this));
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        }
    }

    public void onLoginRequestDone(ParseObject parseObject, ParseException e) {
        hideKeyBoard();
        if (parseObject != null) {
            userEntity = UserMapper.getUserEntityFromParseObject(parseObject);
            mMaxAttempt = DUCacheHelper.getDuCacheHelper().getSettingEntity().getMaxAttempts();
            //verifying loginattempt and maximum attempt from DU cache helper and then performing login else showing alert.
            if (userEntity.getAttempt() < mMaxAttempt) {
                requestCode = FETCH_DEVICE_FOR_MAC_ADDRESS_REQUEST;
                //Fetch device for  particular user
                fetchDevice();
            } else {
                showMaximumAttemptsFailure();
                hideProgressDialog();
            }

        } else if (e != null && e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(LoginActivity.this);
            hideProgressDialog();
            btnLogin.setText(getString(R.string.activity_login_sign_in));
        } else {
            //On the basis of code from parse am displaying error.
            switch (e.getCode()) {
                case Utility.OBJECT_NOT_FOUND:
                    requestCode = INCREMENT_ATTEMPT_REQUEST;
                    incrementAttempts();
                    errorMsg = getString(R.string.invalid_login_parameters);
                    mUsername.requestFocus();
                    break;
                default:
                    errorMsg = getString(R.string.login_failed);
                    mUsername.requestFocus();
                    showAlertLoginFailure(errorMsg);
                    break;
            }
        }
    }

    //Alert to show both login credentials
    public void showAlertCredentials() {
        showAlertDialog(null, getString(R.string.enter_username_pwd), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
        hideKeyBoard();
        hideProgressDialog();
        mUsername.requestFocus();
        btnLogin.setText(getString(R.string.activity_login_sign_in));
    }

    //Alert to show login failure
    public void showAlertLoginFailure(String errorMsg) {
        showAlertDialog(null, errorMsg, null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
        hideKeyBoard();
        hideProgressDialog();
        mUsername.requestFocus();
        btnLogin.setText(getString(R.string.activity_login_sign_in));

    }

    //alert to show alert when we have crossed maximum attempt to do login.
    public void showMaximumAttemptsFailure() {
        showAlertDialog(null, getString(R.string.max_attempt), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
        hideKeyBoard();
        hideProgressDialog();
        mUsername.requestFocus();
        btnLogin.setText(getString(R.string.activity_login_sign_in));
    }

    //To show progressDialog
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        mLoginStatusView.setVisibility(View.VISIBLE);
        mLoginStatusView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginStatusView.setVisibility(show ? View.VISIBLE
                                : View.GONE);
                    }
                });
    }

    //Method to fetch device MAC address of particular device and update the information of the user tagged with that particular device in Device table
    public void fetchDevice() {
        getPresenter().fetchMacAddress(this, this);
    }

    private void deviceNotRegistered(final boolean isAdmin) {
        String msg = isAdmin ? getString(R.string.device_not_registered_admin) : getString(R.string.device_not_registered);
        showAlertDialog(getString(R.string.app_name), msg, null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                if (isAdmin) {
                    Intent addNewUser = new Intent(LoginActivity.this, NewDeviceActivity.class);
                    addNewUser.putExtra(Utility.USER_TABLE, userEntity);
                    addNewUser.putExtra(Utility.MAC_ADDRESS, Utils.retrieveMacAddress(LoginActivity.this));
                    addNewUser.putExtra(Utility.DEVICE_OS, "Android");
                    startActivity(addNewUser);
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    private void deviceInactive(String deviceName) {
        showAlertDialog(getString(R.string.app_name), String.format(getString(R.string.device_inactive), deviceName), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
    }

    //Method to pass the value afterlogin to Sessions table.
    public void addToSessionsAfterSuccessfulLogin(final ParseObject deviceObject) {
        Date date = new Date();
        final SessionsEntity sessionsEntity = new SessionsEntity();
        sessionsEntity.setUserEntity(userEntity);
        sessionsEntity.setDeviceEntity(DeviceMapper.getDeviceEntityFromParseObject(deviceObject));
        sessionsEntity.setLoginTime(date);
        sessionsEntity.setLoggedIn(true);

        final ParseObject parseObject = SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity);

        getPresenter().addToSession(parseObject, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sessionsEntity.setObjectId(parseObject.getObjectId());
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DeviceTrackerApplication.getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Utility.OBJECT_ID, sessionsEntity.getObjectId());
                    editor.commit();
                    DUCacheHelper.getDuCacheHelper().setSessionsEntity(sessionsEntity);

                    updateUserTagged(deviceObject, parseObject);
                } else if (e != null && e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(LoginActivity.this);
                    hideProgressDialog();
                    btnLogin.setText(getString(R.string.activity_login_sign_in));
                } else if (e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(LoginActivity.this);
                    hideProgressDialog();
                    btnLogin.setText(getString(R.string.activity_login_sign_in));
                } else {
                    showAlertLoginFailure(errorMsg = getString(R.string.login_failed));
                }
            }
        });
    }

    //Method to save the user details of the user logged in tagged with that particular device in Device table
    public void updateUserTagged(final ParseObject parseObject, ParseObject sessionObject) {
        getPresenter().saveUserDetails(UserMapper.getParseObjectFromUserEntity(userEntity), parseObject, sessionObject, new SaveCallback() {
            @Override
            //adding row or entry to session table.
            public void done(ParseException e) {
                if (e != null && e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(LoginActivity.this);
                    hideProgressDialog();
                    btnLogin.setText(getString(R.string.activity_login_sign_in));
                } else if (e == null) {
                    userEntity.setAttempt(0);
                    getPresenter().saveObject(UserMapper.getParseObjectFromUserEntity(userEntity), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                hideProgressDialog();
                                btnLogin.setText(getString(R.string.activity_login_sign_in));
                                handleAlerts();
                                if (userEntity.isAdmin()) {
                                    Intent intentMenu = new Intent(LoginActivity.this, MenuActivity.class);
                                    startActivity(intentMenu);
                                } else {
                                    Intent intentWelcome = new Intent(LoginActivity.this, WelcomeUserActivity.class);
                                    intentWelcome.putExtra(Utility.USER_TABLE, userEntity);
                                    startActivity(intentWelcome);
                                }
                            } else if (e.getCode() == Utility.TIME_OUT) {
                                Utils.showRequestTimeOutError(LoginActivity.this);
                            } else {
                                showAlertLoginFailure(getString(R.string.login_failed));
                            }
                        }
                    });
                } else if (e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(LoginActivity.this);
                    hideProgressDialog();
                    btnLogin.setText(getString(R.string.activity_login_sign_in));
                } else {
                    showAlertLoginFailure(getString(R.string.login_failed));
                }
            }
        });
    }

    private void handleAlerts() {
        LoggedInCheckService.removeCallbacks();
        Intent intent = new Intent(this, LoggedInCheckService.class);
        intent.putExtra(Utility.ALERT_TYPE, Utility.SESSION_TIMEOUT_ALERT);
        startService(intent);
    }

    //Method to increment the attempt column in Login Attempt table when user tries to log in with a wrong credential
    public void incrementAttempts() {
        String username = mUsername.getText().toString().trim();
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.USERNAME, username);
        getPresenter().fetchRecordForKeys(Utility.USER_TABLE, map, this);
    }


    //Hide Progress Dialog
    @Override
    public void hideProgressDialog() {
        showProgress(false);
        super.hideProgressDialog();
    }

    //Method when focus is changed for edit text password scroll view will set to btn login and btn login's height.
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.et_password:
                    svLogin.scrollTo(0, btnLogin.getBottom() + btnLogin.getHeight());
                    break;
            }
        }
    }

    //Method to add listener to password edittext.
    private void addListeners() {
        mPassword.setOnFocusChangeListener(this);
        btnLogin.setOnClickListener(this);
        tvAboutUs.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
    }

    private void onAboutUsClick() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void onHelpClick() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_login_button:
                onLoginButtonClick();
                break;
            case R.id.tv_about_us:
                onAboutUsClick();
                break;
            case R.id.tv_help:
                onHelpClick();
                break;
        }
    }

}
