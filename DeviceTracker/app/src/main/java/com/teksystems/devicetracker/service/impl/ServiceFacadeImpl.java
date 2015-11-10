package com.teksystems.devicetracker.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.service.ServiceAddNewProject;
import com.teksystems.devicetracker.service.ServiceAddNewUser;
import com.teksystems.devicetracker.service.ServiceBase;
import com.teksystems.devicetracker.service.ServiceFacade;
import com.teksystems.devicetracker.service.ServiceLogOut;
import com.teksystems.devicetracker.service.ServiceLogin;
import com.teksystems.devicetracker.service.ServiceSetting;
import com.teksystems.devicetracker.service.ServiceShowUsers;
import com.teksystems.devicetracker.service.ServiceSplash;
import com.teksystems.devicetracker.util.Utility;

import java.util.Map;

/**
 * Implementation for the service facade.
 */
@Singleton
public class ServiceFacadeImpl implements ServiceFacade {
    @Inject
    private ServiceLogin mServiceLogin;
    @Inject
    private ServiceLogOut mServiceLogOut;
    @Inject
    private ServiceSetting mServiceSetting;
    @Inject
    private ServiceAddNewUser serviceAddNewUser;
    @Inject
    private ServiceBase serviceBase;
    @Inject
    private ServiceSplash mServiceSplash;
    @Inject
    private ServiceShowUsers serviceShowUsers;
    @Inject
    private ServiceAddNewProject mServiceAddNewProject;

    @Override
    public void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback) {
        serviceBase.fetchAll(pTable, pFindCallback);
    }

    //Method to fetch the record from a particular table based on particular key
    @Override
    public void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback) {
        serviceBase.fetchRecordForKey(pTable, pKey, pValue, pGetCallback);
    }

    @Override
    public void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback, String... includeKeys) {
        serviceBase.fetchRecordForKeys(pTable, pMap, pGetCallback, includeKeys);
    }


    @Override
    public void addObject(ParseObject parseObject, SaveCallback pSaveCallback) {
        serviceBase.addObject(parseObject, pSaveCallback);
    }

    @Override
    public void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback) {
        serviceBase.deleteRecordForKey(parseObject, pDeleteCallback);
    }

    @Override
    public void getSettingValues(String pTable, FindCallback pFindCallback) {
        mServiceSplash.fetchAll(pTable, pFindCallback);
    }

    //Method to fetch number of attempts by calling fetchRecordForKey for a particular table.
    @Override
    public void fetchAttempts(String username, GetCallback<ParseObject> getCallback) {
        mServiceLogin.fetchRecordForKey(Utility.LOGIN_ATTEMPT, Utility.USERNAME, username, getCallback);
    }

    @Override
    public void update(String username, GetCallback getCallback) {
        mServiceLogin.update(username, getCallback);
    }

    @Override
    public void updateSettingValues(ParseObject parseObject, SaveCallback pSaveCallback) {
        mServiceSetting.addObject(parseObject, pSaveCallback);
    }

    @Override
    public void saveProjectDetails(ParseObject parseObject, SaveCallback pSaveCallback) {
        mServiceAddNewProject.addObject(parseObject, pSaveCallback);
    }

    public void fetchAllIncludingPointers(String pTable, FindCallback<ParseObject> pFindCallback, String... includeKeys) {
        serviceBase.fetchAll(pTable, pFindCallback, includeKeys);
    }

    @Override
    public void fetchRecordsForKeys(String pTable, Map<String, Object> pMap, FindCallback pFindCallback, String... includeKeys) {
        serviceBase.fetchRecordsForKeys(pTable, pMap, pFindCallback, includeKeys);
    }
}
