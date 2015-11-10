package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.teksystems.devicetracker.service.impl.ServiceFacadeImpl;

import java.util.Map;

/**
 * Interface for the service facade.
 */
@ImplementedBy(ServiceFacadeImpl.class)
public interface ServiceFacade {

    void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback);

    void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback);

    void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback, String... includeKeys);

    void addObject(ParseObject parseObject, SaveCallback pSaveCallback);

    void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback);

    void getSettingValues(String pTable, FindCallback pFindCallback);

    void fetchAttempts(String username, GetCallback<ParseObject> getCallback);

    void update(String username, GetCallback getCallback);

    void updateSettingValues(ParseObject parseObject, SaveCallback pSaveCallback);

    void saveProjectDetails(ParseObject parseObject, SaveCallback pSaveCallback);

    void fetchAllIncludingPointers(String pTable, FindCallback<ParseObject> pFindCallback, String... includeKyes);

    void fetchRecordsForKeys(String pTable, Map<String, Object> pMap, FindCallback pFindCallback, String... includeKeys);

}
