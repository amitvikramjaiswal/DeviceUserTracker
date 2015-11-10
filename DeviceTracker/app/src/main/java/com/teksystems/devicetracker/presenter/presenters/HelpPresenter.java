package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.HelpPresenterImpl;
import com.teksystems.devicetracker.view.views.HelpView;

/**
 * Created by ajaiswal on 10/29/2015.
 */
@ImplementedBy(HelpPresenterImpl.class)
public abstract class HelpPresenter extends BasePresenter<HelpView>{
}
