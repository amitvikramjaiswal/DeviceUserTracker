package com.teksystems.devicetracker.presenter.presenters.impl;

import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.presenters.ResetUserPresenter;

/**
 * Created by ajaiswal on 9/30/2015.
 */
public class ResetUserPresenterImpl extends ResetUserPresenter {
    @Override
    public void addObject(ParseObject parseObject, SaveCallback pSaveCallback) {
        getServiceFacade().addObject(parseObject,pSaveCallback);
    }
}
