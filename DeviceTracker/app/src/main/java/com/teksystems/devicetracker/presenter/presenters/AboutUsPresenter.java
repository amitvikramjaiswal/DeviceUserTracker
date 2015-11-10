package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.AboutUsPresenterImpl;
import com.teksystems.devicetracker.view.views.AboutUsView;

/**
 * Created by ajaiswal on 10/29/2015.
 */
@ImplementedBy(AboutUsPresenterImpl.class)
public abstract class AboutUsPresenter extends BasePresenter<AboutUsView>{
}
