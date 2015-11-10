package com.teksystems.devicetracker.view.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.presenter.presenters.NewUserPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.NewUserView;

import java.util.Date;

import roboguice.inject.InjectView;

public class NewUserActivity extends BaseActivity<NewUserView, NewUserPresenter>
        implements NewUserView, View.OnClickListener, TextWatcher, SaveCallback, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    @InjectView(R.id.sv_new_user)
    private ScrollView svNewUser;
    @InjectView(R.id.et_name)
    private EditText etName;
    @InjectView(R.id.et_username)
    private EditText etUsername;
    @InjectView(R.id.et_password)
    private EditText etPassword;
    @InjectView(R.id.et_confirm_password)
    private EditText etConfirmPassword;
    @InjectView(R.id.cb_is_admin)
    private CheckBox cbIsAdmin;
    @InjectView(R.id.btn_save)
    private Button btnSave;
    @InjectView(R.id.btn_cancel)
    private Button btnCancel;
    @InjectView(R.id.cb_show_passwords)
    private CheckBox cbShowPassword;
    @InjectView(R.id.cb_is_enabled)
    private CheckBox cbIsEnabled;
    private String defaultPassword;
    private Context mContext;
    private UserEntity userEntity;
    //    private UserEntity duplicateUserEntity;
    private String mode;
    private boolean mIsChecked;
    private boolean isLoggedInUser;
    private boolean shouldLogout;
    private String mUserNameUpdate;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        mContext = this;

        if (getIntent().hasExtra(Utility.USER_TABLE)) {
            userEntity = (UserEntity) getIntent().getExtras().getSerializable(Utility.USER_TABLE);
            mUserNameUpdate = getIntent().getStringExtra(Utility.USERNAME);
            mode = Utility.EDIT_MODE;
            if (userEntity.getUsername().equalsIgnoreCase(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity().getUsername())) {
                isLoggedInUser = true;
            }
            else {
                isLoggedInUser = false;
            }
            getSupportActionBar().setTitle(R.string.reset_user);
            populateValues();
        }
        else {
            mode = Utility.SAVE_MODE;
            getSupportActionBar().setTitle(R.string.new_user);
            userEntity = new UserEntity();
            defaultPassword = DUCacheHelper.getDuCacheHelper().getSettingEntity().getDefaultPassword();
            populateFields();
        }
        addListeners();
    }

    private void populateValues() {
        btnSave.setText(getString(R.string.update_button));
        etName.setText(userEntity.getName());
        etUsername.setText(userEntity.getUsername());
        etPassword.setText(userEntity.getPassword());
        etConfirmPassword.setText(userEntity.getPassword());
        cbIsAdmin.setChecked(userEntity.isAdmin());
        cbIsEnabled.setChecked(userEntity.isEnabled());
    }

    private void populateFields() {
        etPassword.setText(defaultPassword);
        etConfirmPassword.setText(defaultPassword);
        cbIsEnabled.setChecked(true);
    }

    private void addListeners() {
        etConfirmPassword.setOnFocusChangeListener(this);
        etUsername.addTextChangedListener(this);
        etName.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);
        cbShowPassword.setOnCheckedChangeListener(this);
        cbIsAdmin.setOnCheckedChangeListener(this);
        cbIsEnabled.setOnCheckedChangeListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    public void addNewUser() {
        getPresenter().addObject(UserMapper.getParseObjectFromUserEntity(userEntity), NewUserActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                onSaveButtonClick();
                break;
            case R.id.btn_cancel:
                onCancelButtonClick();
                break;
        }
    }

    private void onSaveButtonClick() {
        String strUsername = etUsername.getText().toString().toLowerCase().trim();
        String strName = etName.getText().toString().toLowerCase().trim();
        String strPassword = etPassword.getText().toString().trim();
        String strConfirmPassword = etConfirmPassword.getText().toString().trim();
        boolean blnIsAdmin = cbIsAdmin.isChecked();
        boolean blnIsEnabled = cbIsEnabled.isChecked();

        userEntity.setName(strName);
        userEntity.setIsAdmin(blnIsAdmin);
        userEntity.setUsername(strUsername);
        userEntity.setPassword(strPassword);
        userEntity.setIsEnabled(blnIsEnabled);

        if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
            userEntity.setAttempt(0);
        }

        if (isLoggedInUser && !blnIsEnabled) {
            shouldLogout = true;
            showDeleteAlert();
        }
        else {
            shouldLogout = false;
            hideKeyBoard();
            showProgressDialog("");
            checkIfUsernameExists();
        }
    }

    private void showDeleteAlert() {
        showAlertDialog(mContext.getString(R.string.app_name), String.format(getString(R.string.editing_loggedin_user_confirmation), userEntity.getUsername()), null, null, mContext.getString(R.string.ok_msg), mContext.getString(R.string.cancel_msg), new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                hideKeyBoard();
                showProgressDialog("");
                checkIfUsernameExists();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    public void checkIfUsernameExists() {
        getPresenter().fetchRecordForKey(Utility.USER_TABLE, Utility.USERNAME, userEntity.getUsername(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null && e.getCode() == Utility.OBJECT_NOT_FOUND) {
                    addNewUser();
                }
                else if (e != null && e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(NewUserActivity.this);
                }
                else if (e != null && e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(NewUserActivity.this);
                }
                else {
                    if (parseObject.getObjectId().equals(userEntity.getObjectId())) {
                        addNewUser();
                    }
                    else {
                        ParseException parseException = new ParseException(202, "User already exits.");
                        errorAddingUser(parseException);
                    }
                }
            }
        });
    }

    private void onCancelButtonClick() {
        onBackPressed();
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            userAddedSuccessfully();
        }
        else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(NewUserActivity.this);
        }
        else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(this);
        }
        else if (e.getCode() == Utility.OBJECT_NOT_FOUND) {
            userNotFoundAlert();
        }
        else {
            Utils.showUnexpectedError(NewUserActivity.this);
        }
    }

    private void userNotFoundAlert() {
        showAlertDialog(null, getString(R.string.user_not_found, userEntity.getUsername()), null, null,
                getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        onBackPressed();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
        hideProgressDialog();
    }

    private void errorAddingUser(ParseException exception) {
        hideProgressDialog();
        switch (exception.getCode()) {
            case Utility.USERNAME_ALREADY_EXISTS:
                showAlertDialog(getString(R.string.app_name), String.format(getString(R.string.user_already_exists), userEntity.getUsername()), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        etUsername.requestFocus();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
                break;
            default:
                showAlertDialog(getString(R.string.app_name), exception.getMessage(), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        etUsername.requestFocus();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
                break;
        }
    }

    private void userAddedSuccessfully() {
        hideProgressDialog();
        String msg = "";
        if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
            msg = String.format(getString(R.string.user_added_successfully), userEntity.getUsername());
        }
        else if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
            msg = String.format(getString(R.string.user_updated_successfully), mUserNameUpdate);
        }
        btnSave.setEnabled(false);
        showAlertDialog(getString(R.string.app_name), msg, null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                if (shouldLogout) {
                    logoutFromTheApp();
                }

                 /*Update the userName after successful update*/
                mUserNameUpdate = etUsername.getText().toString().trim();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    private void logoutFromTheApp() {
        showProgressDialog("");
        SessionsEntity sessionsEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity();
        if (sessionsEntity == null) {
            return;
        }
        sessionsEntity.setLogoutTime(new Date());
        sessionsEntity.setLoggedIn(false);
        getPresenter().addObject(SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressDialog();
                if (e == null) {
                    Utils.clearUserData();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(NewUserActivity.this);
                }
                else if (e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(NewUserActivity.this);
                }
                else {
                    Utils.showUnexpectedError(NewUserActivity.this);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkFieldsForEmptyValues();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isPasswordMatching(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        return false;
    }

    private void checkFieldsForEmptyValues() {
        String strUsername = etUsername.getText().toString().trim();
        String strName = etName.getText().toString().trim();
        String strPassword = etPassword.getText().toString().trim();
        String strConfirmPassword = etConfirmPassword.getText().toString().trim();
        boolean blnIsAdmin = cbIsAdmin.isChecked();
        boolean blnIsEnabled = cbIsEnabled.isChecked();

        if (strName == null || strUsername == null || strPassword == null || strConfirmPassword == null) {
            btnSave.setEnabled(false);
        }
        else if (strName.isEmpty() || strUsername.isEmpty() || strPassword.isEmpty() || strConfirmPassword.isEmpty()) {
            btnSave.setEnabled(false);
        }
        else if (!isPasswordMatching(strPassword, strConfirmPassword)) {
            btnSave.setEnabled(false);
        }
        else {
            if (strPassword.length() < 6 || strConfirmPassword.length() < 6) {
                btnSave.setEnabled(false);
            }
            else if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
                if (strUsername.equalsIgnoreCase(userEntity.getUsername()) && strName.equalsIgnoreCase(userEntity.getName()) && strPassword.equalsIgnoreCase(userEntity.getPassword()) && strConfirmPassword.equalsIgnoreCase(userEntity.getPassword()) && blnIsAdmin == userEntity.isAdmin() && blnIsEnabled == userEntity.isEnabled()) {
                    btnSave.setEnabled(false);
                }
                else {
                    btnSave.setEnabled(true);
                }
            }
            else {
                btnSave.setEnabled(true);
            }
        }

        hideOrShowPassWord(mIsChecked);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.et_confirm_password:
                    svNewUser.fullScroll(View.FOCUS_DOWN);
                    break;
            }
        }
    }

    /*Method to hide and show password*/
    private void hideOrShowPassWord(boolean isChecked) {
        mIsChecked = isChecked;
        if (!etPassword.getText().toString().isEmpty()) {
            if (isChecked) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }

        if (!etConfirmPassword.getText().toString().isEmpty()) {
            if (isChecked) {
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_show_passwords:
                hideOrShowPassWord(isChecked);
                break;
            case R.id.cb_is_admin:
            case R.id.cb_is_enabled:
                String strUsername = etUsername.getText().toString().trim();
                String strName = etName.getText().toString().trim();
                String strPassword = etPassword.getText().toString().trim();
                String strConfirmPassword = etConfirmPassword.getText().toString().trim();
                boolean blnIsAdmin = cbIsAdmin.isChecked();
                boolean blnIsEnabled = cbIsEnabled.isChecked();

                if (mode.equals(Utility.EDIT_MODE)) {
                    if (strUsername.equalsIgnoreCase(userEntity.getUsername()) && strName.equalsIgnoreCase(userEntity.getName()) && strPassword.equalsIgnoreCase(userEntity.getPassword()) && strConfirmPassword.equalsIgnoreCase(userEntity.getPassword()) && blnIsAdmin == userEntity.isAdmin() && blnIsEnabled == userEntity.isEnabled()) {
                        btnSave.setEnabled(false);
                    }
                    else {
                        btnSave.setEnabled(true);
                    }
                }
                break;
        }
    }

}
