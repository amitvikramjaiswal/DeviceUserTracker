package com.teksystems.devicetracker.presenter.presenters.impl;

import com.parse.LogOutCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.presenters.WelcomeUserPresenter;

/**
 * Created by sprasar on 9/18/2015.
 */
public class WelcomeUserPresenterImpl extends WelcomeUserPresenter {
    @Override
    public void addObject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject,saveCallback);
    }
}
