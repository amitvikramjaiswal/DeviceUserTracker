package com.teksystems.devicetracker.view.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.presenter.presenters.ChangePasswordPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.ChangePasswordView;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by sprasar on 10/6/2015.
 */
public class ChangePasswordActivity extends BaseActivity<ChangePasswordView, ChangePasswordPresenter> implements ChangePasswordView, FindCallback<ParseObject>, SaveCallback, View.OnClickListener, TextWatcher {
    @InjectView(R.id.sv_change_pwd)
    private ScrollView svChangePwd;
    @InjectView(R.id.et_old_pwd)
    private EditText mOldPwd;
    @InjectView(R.id.et_password)
    private EditText mPassword;
    @InjectView(R.id.et_confirm_password)
    private EditText mConfirmPassword;
    @InjectView(R.id.btn_cancel)
    private Button mCancel;
    @InjectView(R.id.btn_save)
    private Button mSave;
    private UserEntity userEntity;
    private List<UserEntity> arlUsers;
    private String oldPassword;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.change_pwd);
        setTitle(getString(R.string.change_pwd_title));
        addListeners();
        if (getIntent().getExtras() != null) {
            userEntity = (UserEntity) getIntent().getExtras().getSerializable(Utility.USER_TABLE);
        }
    }

    private void checkFieldsForEmptyValues() {
        String strOldPassword = mOldPwd.getText().toString().trim();
        String strPassword = mPassword.getText().toString().trim();
        String strConfirmPassword = mConfirmPassword.getText().toString().trim();

        if (strOldPassword == null || strPassword == null || strConfirmPassword == null) {
            mSave.setEnabled(false);
        } else if (strOldPassword.isEmpty() || strPassword.isEmpty() || strConfirmPassword.isEmpty()) {
            mSave.setEnabled(false);
        } else if (!strPassword.equals(strConfirmPassword)) {
            mSave.setEnabled(false);
        } else if (strPassword.length() < Utility.SETTING_MIN_DEFAULT_LENGTH) {
            mSave.setEnabled(false);
        } else if (strConfirmPassword.length() < Utility.SETTING_MIN_DEFAULT_LENGTH) {
            mSave.setEnabled(false);

        } else if (!userEntity.getPassword().equals(strOldPassword)) {
            mSave.setEnabled(false);

        } else {
            mSave.setEnabled(true);
        }
    }

    private void onSaveButtonClick() {
        oldPassword = mPassword.getText().toString();
        userEntity.setPassword(oldPassword);
        getPresenter().addObject(UserMapper.getParseObjectFromUserEntity(userEntity), this);
    }

    public void passwordChangedSuccessfully() {
        showAlertDialog(null, getString(R.string.password_change_successful), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                Intent login = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(login);
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });

    }

    @Override
    public void done(List<ParseObject> list, ParseException e) {

    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            passwordChangedSuccessfully();
            hideProgressDialog();
        } else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(ChangePasswordActivity.this);
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(ChangePasswordActivity.this);
        } else {
            Utils.showUnexpectedError(ChangePasswordActivity.this);
        }
    }

    private void addListeners() {
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

        mOldPwd.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mConfirmPassword.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                showProgressDialog("");
                onSaveButtonClick();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
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
}
