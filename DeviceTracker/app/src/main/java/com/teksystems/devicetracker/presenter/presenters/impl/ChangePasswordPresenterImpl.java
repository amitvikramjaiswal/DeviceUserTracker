package com.teksystems.devicetracker.presenter.presenters.impl;

import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.presenters.ChangePasswordPresenter;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by sprasar on 10/6/2015.
 */
public class ChangePasswordPresenterImpl extends ChangePasswordPresenter {
   
    @Override
    public void addObject(ParseObject parseObject, SaveCallback pSaveCallback) {
      getServiceFacade().addObject(parseObject,pSaveCallback);
    }
}
