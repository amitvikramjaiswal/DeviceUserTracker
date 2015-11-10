package com.teksystems.devicetracker.presenter.presenters.impl;

import android.content.Context;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.LoginAttemptEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.presenter.presenters.LoginPresenter;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sprasar on 9/22/2015.
 */
public class LoginPresenterImpl extends LoginPresenter {
    UserEntity userEntity;
    private Context mContext;

    @Override
    public void fetchAttempts(String username, GetCallback<ParseObject> getCallback) {
        getServiceFacade().fetchRecordForKey(Utility.LOGIN_ATTEMPT, Utility.USERNAME, username, getCallback);
    }

    @Override
    public void update(String username, GetCallback getCallback) {
        getServiceFacade().update(username, getCallback);
    }

    @Override
    public void saveAttempts(LoginAttemptEntity loginAttemptEntity, SaveCallback saveCallBack) {
        ParseObject parseObject = new ParseObject(Utility.LOGIN_ATTEMPT);
        parseObject.setObjectId(loginAttemptEntity.getObjectId());
        parseObject.put(Utility.ATTEMPT, loginAttemptEntity.getAttempt());
        getServiceFacade().addObject(parseObject, saveCallBack);
    }

    public void saveObject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKeys(pTable, pMap, pGetCallback);
    }

    //Method to fetch the mac address of device
    @Override
    public void fetchMacAddress(Context context, GetCallback<ParseObject> getCallback) {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.MAC_ADDRESS, Utils.retrieveMacAddress(context));
        getServiceFacade().fetchRecordForKeys(Utility.DEVICE, map, getCallback, Utility.TAGGED_PROJECT, Utility.TAGGED_USER);
    }

    @Override
    public void saveUserDetails(ParseObject parseUser, ParseObject parseObject, ParseObject sessionObject, SaveCallback saveCallBack) {
        parseObject.put(Utility.TAGGED_USER, parseUser);
        parseObject.put(Utility.LAST_ACTIVE_SESSION, sessionObject);
        getServiceFacade().addObject(parseObject, saveCallBack);
    }

    //Method to add one column to Sessions table.
    @Override
    public void addToSession(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void customLogin(String username, String password, GetCallback<ParseObject> getCallback) {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.USERNAME, username);
        map.put(Utility.PASSWORD, password);
        map.put(Utility.IS_ENABLED, true);
        getServiceFacade().fetchRecordForKeys(Utility.USER_TABLE, map, getCallback);
    }


}
