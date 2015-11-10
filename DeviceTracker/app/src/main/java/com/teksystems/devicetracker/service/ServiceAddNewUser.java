package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.teksystems.devicetracker.service.impl.ServiceAddNewUserImpl;

/**
 * Created by ajaiswal on 9/22/2015.
 */
@ImplementedBy(ServiceAddNewUserImpl.class)
public interface ServiceAddNewUser extends ServiceBase {

}
