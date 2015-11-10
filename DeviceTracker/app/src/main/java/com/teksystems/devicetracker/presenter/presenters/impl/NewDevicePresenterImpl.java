package com.teksystems.devicetracker.presenter.presenters.impl;

import android.content.Context;

import com.google.inject.Inject;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.presenters.NewDevicePresenter;

import java.util.Map;

/**
 * Created by ajaiswal on 10/6/2015.
 */
public class NewDevicePresenterImpl extends NewDevicePresenter {

    private static final String LOG_TAG = "NewDevicePresenterImpl";

    private Context mContext;

    @Inject
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void addObject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback) {
        getServiceFacade().fetchAll(pTable, pFindCallback);
    }

    @Override
    public void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKeys(pTable, pMap, pGetCallback);
    }

    @Override
    public void deleteRecord(ParseObject parseObject, DeleteCallback pDeleteCallback) {
        getServiceFacade().deleteRecordForKey(parseObject, pDeleteCallback);
    }
}
