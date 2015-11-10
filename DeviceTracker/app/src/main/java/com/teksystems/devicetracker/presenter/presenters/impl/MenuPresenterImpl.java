package com.teksystems.devicetracker.presenter.presenters.impl;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.SettingEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.presenter.presenters.MenuPresenter;
import com.teksystems.devicetracker.util.Utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by akokala on 14-9-15.
 */
public class MenuPresenterImpl extends MenuPresenter {

    @Override
    public void saveSettingValues(SettingEntity settingEntity, SaveCallback saveCallback) {
        ParseObject parseObject = new ParseObject(Utility.SETTING);
        parseObject.setObjectId(settingEntity.getObjectId());
        parseObject.put(Utility.SESSION_TIMEOUT, settingEntity.getSessionTimeout());
        parseObject.put(Utility.ALERT_INTERVAL, settingEntity.getAlertInterval());
        parseObject.put(Utility.MAX_ATTEMPTS, settingEntity.getMaxAttempts());
        parseObject.put(Utility.DEFAULT_PASSWORD, settingEntity.getDefaultPassword());
        getServiceFacade().updateSettingValues(parseObject, saveCallback);
    }

    @Override
    public void fetchAllRecords(String pTable, FindCallback<ParseObject> pFindCallback, String... includeKeys) {
        getServiceFacade().fetchAllIncludingPointers(pTable, pFindCallback, includeKeys);
    }

    @Override
    public void addObject(ParseObject parseObject, SaveCallback saveCallback) {
        getServiceFacade().addObject(parseObject, saveCallback);
    }

    @Override
    public void fetchRecordsForKeys(String pTable, Map<String,Object> pMap,FindCallback<ParseObject> pFindCallback, String... includeKeys) {
        getServiceFacade().fetchRecordsForKeys(pTable, pMap, pFindCallback, includeKeys);
    }

    @Override
    public void fetchRecordsForKeysForAndroid(FindCallback<ParseObject> pFindCallback, String... includeKeys) {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.DEVICE_OS, Utility.ANDROID_OS);
        getServiceFacade().fetchRecordsForKeys(Utility.DEVICE, map, pFindCallback, includeKeys);
    }

    @Override
    public void fetchRecordsForKeysForIOS(FindCallback<ParseObject> pFindCallback, String... includeKeys) {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.DEVICE_OS, Utility.IOS_OS);
        getServiceFacade().fetchRecordsForKeys(Utility.DEVICE, map, pFindCallback, includeKeys);
    }

    @Override
    public void fetchRecordForKeys(String pTable, Map<String, Object> map, FindCallback<ParseObject> pGetCallback, String... includeKeys) {
        getServiceFacade().fetchRecordsForKeys(pTable, map, pGetCallback, includeKeys);
    }

    public void findObjectForKey(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKeys(pTable, pMap, pGetCallback);
    }

    @Override
    public void deleteObject(ParseObject parseObject, DeleteCallback deleteCallback) {
        getServiceFacade().deleteRecordForKey(parseObject, deleteCallback);
    }

    @Override
    public void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback) {
        getServiceFacade().fetchRecordForKey(pTable, pKey, pValue, pGetCallback);
    }

    @Override
    public void saveTaggedUserValues(DeviceEntity deviceEntity, SaveCallback saveCallback) {
        ParseObject parseObject = DeviceMapper.getParseObjectFromDeviceEntity(deviceEntity);
        getServiceFacade().addObject(parseObject, saveCallback);
    }
}
