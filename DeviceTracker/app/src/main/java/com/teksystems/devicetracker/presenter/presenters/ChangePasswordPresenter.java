package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.ChangePasswordPresenterImpl;
import com.teksystems.devicetracker.view.views.ChangePasswordView;

/**
 * Created by sprasar on 10/6/2015.
 */
@ImplementedBy(ChangePasswordPresenterImpl.class)
public abstract class ChangePasswordPresenter extends BasePresenter<ChangePasswordView> {
    public abstract void addObject(ParseObject parseObject, SaveCallback pSaveCallback);
}
