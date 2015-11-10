package com.teksystems.devicetracker.service.impl;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.application.DeviceTrackerApplication;
import com.teksystems.devicetracker.service.ServiceBase;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ajaiswal on 9/22/2015.
 */
public class ServiceBaseImpl implements ServiceBase {

    private ParseException parseException;

    public ServiceBaseImpl() {
        parseException = new ParseException(Utility.NO_NETWORK, DeviceTrackerApplication.getContext().getString(R.string.network_unavailable));
    }

    public ParseException getParseException() {
        return parseException;
    }

    @Override
    public void fetchAll(String pTable, FindCallback pFindCallback, String... includeKeys) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(pTable);
            for (String key : includeKeys) {
                query.include(key);
            }
            query.findInBackground(pFindCallback);
        }
        else {
            pFindCallback.done(null, parseException);
        }
    }

    @Override
    public void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(pTable);
            query.whereEqualTo(pKey, pValue);
            query.getFirstInBackground(pGetCallback);
        }
        else {
            pGetCallback.done(null, parseException);
        }
    }

    @Override
    public void addObject(ParseObject parseObject, SaveCallback pSaveCallback) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            parseObject.saveInBackground(pSaveCallback);
        }
        else {
            pSaveCallback.done(parseException);
        }
    }

    @Override
    public void updateRecord(ParseObject parseObject, GetCallback<ParseObject> pGetCallback, SaveCallback pSaveCallback) {

    }

    @Override
    public void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            parseObject.deleteInBackground(pDeleteCallback);
        }
        else {
            pDeleteCallback.done(parseException);
        }
    }

    @Override
    public void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback, String... includeKeys) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            Set<String> keys = pMap.keySet();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(pTable);
            for (String key : keys) {
                query.whereEqualTo(key, pMap.get(key));
            }
            for (String key : includeKeys) {
                query.include(key);
            }
            query.getFirstInBackground(pGetCallback);
        }
        else {
            pGetCallback.done(null, parseException);
        }
    }

    @Override
    public void fetchRecordsForKeys(String pTable, Map<String, Object> pMap, FindCallback pFindCallback, String... includeKeys) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(pTable);

            Set<String> keys = pMap.keySet();
            for (String key : keys) {
                query.whereEqualTo(key, pMap.get(key));
            }

            for (String key : includeKeys) {
                query.include(key);
            }
            query.findInBackground(pFindCallback);
        }
        else {
            pFindCallback.done(null, parseException);
        }
    }
}
