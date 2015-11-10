package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.NewDevicePresenterImpl;
import com.teksystems.devicetracker.presenter.presenters.impl.NewUserPresenterImpl;
import com.teksystems.devicetracker.view.views.NewDeviceView;

import java.util.Map;

/**
 * Created by ajaiswal on 10/6/2015.
 */
@ImplementedBy(NewDevicePresenterImpl.class)
public abstract class NewDevicePresenter extends BasePresenter<NewDeviceView> {

    public abstract void addObject(ParseObject parseObject, SaveCallback saveCallback);

    public abstract void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback);

    public abstract void fetchRecordForKeys(String pTable, Map<String, Object> map, GetCallback<ParseObject> pGetCallback);

    public abstract void deleteRecord(ParseObject parseObject, DeleteCallback pDeleteCallback);

}
