package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.NewUserPresenterImpl;
import com.teksystems.devicetracker.view.views.NewUserView;

/**
 * Created by ajaiswal on 9/22/2015.
 */
@ImplementedBy(NewUserPresenterImpl.class)
public abstract class NewUserPresenter extends BasePresenter<NewUserView> {

    public abstract void addObject(ParseObject parseObject, SaveCallback saveCallback);

    public abstract void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback);

    public abstract void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback);

    public abstract void addUserToLoginAttempt(String pUsername, SaveCallback pSaveCallback);

    public abstract void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback);

}
