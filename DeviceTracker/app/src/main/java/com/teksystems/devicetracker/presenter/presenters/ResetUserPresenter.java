package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.ResetUserPresenterImpl;
import com.teksystems.devicetracker.view.views.ResetUserView;

/**
 * Created by ajaiswal on 9/30/2015.
 */
@ImplementedBy(ResetUserPresenterImpl.class)
public abstract class ResetUserPresenter extends BasePresenter<ResetUserView> {
    public abstract void addObject(ParseObject parseObject, SaveCallback pSaveCallback);
}
