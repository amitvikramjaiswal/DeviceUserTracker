package com.teksystems.devicetracker.presenter.presenters.impl;


import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.presenters.SplashPresenter;

/**
 * Created by akokala on 18-8-15.
 */
public class SplashPresenterImpl extends SplashPresenter {

    @Override
    public void getSettingValues(String tableName, FindCallback findCallback) {
        getServiceFacade().getSettingValues(tableName, findCallback);
    }

    @Override
    public void saveObject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }
}
