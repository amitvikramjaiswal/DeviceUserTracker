package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.WelcomeUserPresenterImpl;
import com.teksystems.devicetracker.view.views.WelcomeUserView;

/**
 * Created by sprasar on 9/18/2015.
 */
@ImplementedBy(WelcomeUserPresenterImpl.class)
public abstract class WelcomeUserPresenter extends BasePresenter<WelcomeUserView> {
    public abstract void addObject(ParseObject parseObject, SaveCallback saveCallback);
}

