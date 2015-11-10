package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.teksystems.devicetracker.service.impl.ServiceAddNewProjectImpl;

/**
 * Created by akokala on 6-10-15.
 */
@ImplementedBy(ServiceAddNewProjectImpl.class)
public interface ServiceAddNewProject extends ServiceBase {
}
