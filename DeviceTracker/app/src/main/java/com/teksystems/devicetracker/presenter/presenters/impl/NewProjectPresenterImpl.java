package com.teksystems.devicetracker.presenter.presenters.impl;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.presenter.presenters.NewProjectPresenter;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by akokala on 6-10-15.
 */
public class NewProjectPresenterImpl extends NewProjectPresenter {

    @Override
    public void addNewProject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKey(pTable, pKey, pValue, pGetCallback);
    }
}
