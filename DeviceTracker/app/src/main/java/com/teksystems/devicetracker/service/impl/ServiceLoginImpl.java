package com.teksystems.devicetracker.service.impl;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.teksystems.devicetracker.application.DeviceTrackerApplication;
import com.teksystems.devicetracker.service.ServiceLogin;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;

/**
 * Created by sprasar on 9/18/2015.
 */
public class ServiceLoginImpl extends ServiceBaseImpl implements ServiceLogin {

    //Method to update the user name attempt in LoginAttempt table.
    @Override
    public void update(String username, GetCallback getCallback) {
        if (Utils.isNetworkAvailable(DeviceTrackerApplication.getContext())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("LoginAttempt");
            query.whereEqualTo(Utility.USERNAME, username);
            query.getFirstInBackground(getCallback);
        } else {
            getCallback.done(null, getParseException());
        }
    }

}
