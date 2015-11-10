package com.teksystems.devicetracker.presenter.presenters.impl;

import android.content.Context;

import com.google.inject.Inject;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.teksystems.devicetracker.presenter.presenters.NewUserPresenter;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.log.Logger;

/**
 * Created by ajaiswal on 9/22/2015.
 */
public class NewUserPresenterImpl extends NewUserPresenter {

    private static final String LOG_TAG = "NewUserPresenterImpl";

    private Context mContext;

    @Inject
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void addObject(ParseObject parseObject, SaveCallback saveCallback) {
        Logger.d(LOG_TAG, "Inside addObject");
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback) {
        getServiceFacade().fetchAll(pTable, pFindCallback);
    }

    @Override
    public void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKey(pTable, pKey, pValue, pGetCallback);
    }

    @Override
    public void addUserToLoginAttempt(String username, SaveCallback pSaveCallback) {
        ParseObject parseObject = new ParseObject(Utility.LOGIN_ATTEMPT);
        parseObject.put(Utility.USERNAME, username);
        parseObject.put(Utility.ATTEMPT, 0);
        getServiceFacade().addObject(parseObject, pSaveCallback);
    }

    @Override
    public void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback) {
        getServiceFacade().deleteRecordForKey(parseObject, pDeleteCallback);
    }
}
