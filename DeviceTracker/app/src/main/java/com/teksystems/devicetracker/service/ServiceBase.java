package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.service.impl.ServiceBaseImpl;

import java.util.Map;

/**
 * Created by ajaiswal on 9/22/2015.
 */
@ImplementedBy(ServiceBaseImpl.class)
public interface ServiceBase {
    /**
     * find all records from db.
     *
     * @param pTable        the table name to query.
     * @param pFindCallback callback method that will be invoked after fetch.
     */
    void fetchAll(String pTable, FindCallback<ParseObject> pFindCallback, String... includeKeys);

    /**
     * finds record based on key.
     *
     * @param pTable       the table name to query.
     * @param pKey         key to search object.
     * @param pValue       value for the key.
     * @param pGetCallback callback method that will be invoked after fetch is complete.
     */
    void fetchRecordForKey(String pTable, String pKey, String pValue, GetCallback<ParseObject> pGetCallback);

    /**
     * adds a new record to db.
     *
     * @param parseObject   object to add.
     * @param pSaveCallback callback method that will be invoked after adding the object.
     */
    void addObject(ParseObject parseObject, SaveCallback pSaveCallback);

    /**
     * updates an existing record.
     *
     * @param parseObject   object to update (new values and the old ones).
     * @param pGetCallback  callback method that will be invoked after finding the updating object.
     * @param pSaveCallback callback method that will be invoked after saving the changes.
     */
    void updateRecord(ParseObject parseObject, GetCallback<ParseObject> pGetCallback, SaveCallback pSaveCallback);

    /**
     * deletes a record if match found.
     *
     * @param parseObject     object to delete.
     * @param pDeleteCallback callback method that will be invoked after deleting.
     */
    void deleteRecordForKey(ParseObject parseObject, DeleteCallback pDeleteCallback);

    /**
     * finds record based on key.
     *
     * @param pTable       the table name to query.
     * @param pMap         key value pair for query.
     * @param pGetCallback callback method that will be invoked after fetch is complete.
     * @param includeKeys  the pointer columns to be included in the result
     */
    void fetchRecordForKeys(String pTable, Map<String, Object> pMap, GetCallback<ParseObject> pGetCallback, String... includeKeys);

    /**
     * @param pTable        the table name to query.
     * @param pMap          key value pair for query.
     * @param pFindCallback callback method that will be invoked after fetch is complete.
     */
    void fetchRecordsForKeys(String pTable, Map<String, Object> pMap, FindCallback pFindCallback, String... includeKeys);
}
