package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.SplashPresenterImpl;
import com.teksystems.devicetracker.view.views.SplashView;

@ImplementedBy(SplashPresenterImpl.class)
public abstract class SplashPresenter extends BasePresenter<SplashView> {

    public abstract void getSettingValues(String tableName,FindCallback<ParseObject> findCallback);

    public abstract void saveObject(ParseObject parseObject, SaveCallback saveCallback);
}
