package com.teksystems.devicetracker.presenter.presenters;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.SettingEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.presenters.impl.MenuPresenterImpl;
import com.teksystems.devicetracker.view.views.MenuView;

import java.util.Date;
import java.util.Map;

/**
 * Created by akokala on 14-9-15.
 */
@ImplementedBy(MenuPresenterImpl.class)
public abstract class MenuPresenter extends BasePresenter<MenuView> {

    public abstract void saveSettingValues(SettingEntity settingEntity, SaveCallback saveCallback);

    public abstract void fetchAllRecords(String pTable, FindCallback<ParseObject> pFindCallback, String... includeKeys);

    public abstract void deleteObject(ParseObject parseObject, DeleteCallback deleteCallback);

    public abstract void fetchRecordsForKeys(String pTable, Map<String,Object> pMap,FindCallback<ParseObject> pFindCallback, String... includeKeys);

    public abstract void findObjectForKey(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback);

    public abstract void addObject(ParseObject parseObject, SaveCallback saveCallback);

    public abstract void fetchRecordsForKeysForAndroid(FindCallback<ParseObject> pFindCallback, String... includeKeys);

    public abstract void fetchRecordsForKeysForIOS(FindCallback<ParseObject> pFindCallback, String... includeKeys);

    public abstract void fetchRecordForKeys(String pTable, Map<String, Object> map, FindCallback<ParseObject> pGetCallback, String... includeKeys);

    public abstract void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback);

    public abstract void saveTaggedUserValues(DeviceEntity deviceEntity, SaveCallback saveCallback);

}
