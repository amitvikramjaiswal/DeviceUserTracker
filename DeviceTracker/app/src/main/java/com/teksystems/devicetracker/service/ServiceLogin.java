package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.teksystems.devicetracker.service.impl.ServiceLoginImpl;

/**
 * Created by sprasar on 9/18/2015.
 */
@ImplementedBy(ServiceLoginImpl.class)
public interface ServiceLogin extends ServiceBase {
     void update(String username,GetCallback getCallback);
}
