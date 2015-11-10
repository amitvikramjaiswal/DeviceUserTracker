package com.teksystems.devicetracker.presenter.presenters;

import android.content.Context;

import com.google.inject.ImplementedBy;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.LoginAttemptEntity;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.LoginPresenterImpl;
import com.teksystems.devicetracker.view.views.LoginView;

import java.util.Map;

@ImplementedBy(LoginPresenterImpl.class)
public abstract class LoginPresenter extends BasePresenter<LoginView> {

    public abstract void fetchAttempts(String username, GetCallback<ParseObject> getCallback);

    public abstract void update(String username, GetCallback getCallback);

    public abstract void saveAttempts(LoginAttemptEntity loginAttemptEntity, SaveCallback saveCallBack);

    public abstract void saveObject(ParseObject parseObject, SaveCallback saveCallback);

    public abstract void fetchRecordForKeys(String pTable, Map<String, Object> map, GetCallback<ParseObject> pGetCallback);

    public abstract void fetchMacAddress(Context context, GetCallback<ParseObject> getCallback);

    public abstract void saveUserDetails(ParseObject parseUser, ParseObject parseObject, ParseObject sessionObject, SaveCallback saveCallBack);

    public abstract void addToSession(ParseObject parseObject, SaveCallback saveCallback);

    public abstract void customLogin(String username, String password, GetCallback<ParseObject> getCallback);
}
