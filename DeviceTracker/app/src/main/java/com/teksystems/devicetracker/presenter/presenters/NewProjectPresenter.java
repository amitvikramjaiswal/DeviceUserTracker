package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.NewProjectPresenterImpl;
import com.teksystems.devicetracker.view.views.NewProjectView;

/**
 * Created by akokala on 6-10-15.
 */
@ImplementedBy(NewProjectPresenterImpl.class)
public abstract class NewProjectPresenter extends BasePresenter<NewProjectView> {

    public abstract void addNewProject(ParseObject parseObject,SaveCallback saveCallback);

    public abstract void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback);
}
