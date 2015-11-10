package com.teksystems.devicetracker.presenter;

import com.google.inject.Inject;
import com.teksystems.devicetracker.service.ServiceFacade;
import com.teksystems.devicetracker.view.views.BaseView;

import java.util.ArrayList;

/**
 * The base class for all presenters.
 *
 * @param <T> The view interface for this presenter.
 */

public abstract class BasePresenter<T extends BaseView> {

    private static ArrayList<BasePresenter<?>> sPresenters = new ArrayList<BasePresenter<?>>();

    private T mView;
    private ServiceFacade mServiceFacade;

    /**
     * Gets the service facade.
     *
     * @return The service facade.
     */
    protected ServiceFacade getServiceFacade() {
        return mServiceFacade;
    }

    /**
     * Sets the service facade.
     *
     * @param serviceFacade The service facade.
     */
    @Inject
    public void setServiceFacade(ServiceFacade serviceFacade) {
        mServiceFacade = serviceFacade;
    }

    /**
     * Gets the view for this presenter.
     */
    protected T getView() {
        return mView;
    }

    public void setView(T view) {
        mView = view;
    }

    /**
     * Call this from the Activity's onCreate method.
     */
    public void onCreate() {
        sPresenters.add(this);
    }

    /**
     * Call this from the Activity's onDestroy method.
     */
    public void onDestroy() {
        sPresenters.remove(this);
    }

    /**
     * Returns the navigation menu category for this activity.
     *
     * @return The navigation category.
     */
    protected int getNavigationMenuCategory() {
        return -1;
    }

    /**
     * Returns true if this activity should have the common navigation menu.
     */
    public boolean hasNavigationMenu() {
        return false;
    }

}