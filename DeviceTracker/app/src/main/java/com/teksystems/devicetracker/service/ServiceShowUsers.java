package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.DeleteCallback;
import com.teksystems.devicetracker.service.impl.ServiceShowUsersImpl;

/**
 * Created by ajaiswal on 9/23/2015.
 */
@ImplementedBy(ServiceShowUsersImpl.class)
public interface ServiceShowUsers extends ServiceBase {
}
