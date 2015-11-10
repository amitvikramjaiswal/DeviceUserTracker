package com.teksystems.devicetracker.view.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.presenter.presenters.WelcomeUserPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.WelcomeUserView;

import java.util.Date;

import roboguice.inject.InjectView;

/**
 * Created by sprasar on 9/18/2015.
 */
public class WelcomeUserActivity extends BaseActivity<WelcomeUserView, WelcomeUserPresenter> implements WelcomeUserView, View.OnClickListener {
    @InjectView(R.id.bt_log_out)
    private ImageView mButton;
    @InjectView(R.id.btn_change_pwd)
    private ImageView mButtonChangePwd;
    private UserEntity userEntity;

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.welcome_user);

        setTitle(getString(R.string.activity_welcome_screen));
        addListeners();
        userEntity = (UserEntity) getIntent().getExtras().getSerializable(Utility.USER_TABLE);
    }

    public void showLogOutAlert() {
        showAlertDialog(null, getString(R.string.logout_confirmation_msg), null, null, getString(R.string.log_out), getString(R.string.cancel_msg), new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                showProgressDialog("");
                logout();

            }

            @Override
            public void onNegativeButtonClicked() {
                hideProgressDialog();
            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }

        });

    }

    public void logout() {
        SessionsEntity sessionsEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity();
        sessionsEntity.setDeviceEntity(null);
        sessionsEntity.setLoginTime(null);
        sessionsEntity.setCreatedAt(null);
        sessionsEntity.setUserEntity(null);
        sessionsEntity.setUpdatedAt(null);
        sessionsEntity.setLogoutTime(new Date());
        sessionsEntity.setLoggedIn(false);
        getPresenter().addObject(SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    hideProgressDialog();
                    Utils.clearUserData();
                    Intent intent = new Intent(WelcomeUserActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(WelcomeUserActivity.this);
                } else if (e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(WelcomeUserActivity.this);
                } else {
                    Utils.showUnexpectedError(WelcomeUserActivity.this);
                }
            }
        });

    }

    public void changePassword() {
        Intent intentWelcome = new Intent(WelcomeUserActivity.this, ChangePasswordActivity.class);
        intentWelcome.putExtra(Utility.USER_TABLE, userEntity);
        startActivity(intentWelcome);
    }

    private void addListeners() {
        mButtonChangePwd.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_log_out:
                showLogOutAlert();
                break;
            case R.id.btn_change_pwd:
                changePassword();
                break;
        }
    }
}

