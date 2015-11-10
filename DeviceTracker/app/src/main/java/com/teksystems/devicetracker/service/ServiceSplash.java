package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.teksystems.devicetracker.service.impl.ServiceSplashImpl;

/**
 * Created by akokala on 23-9-15.
 */
@ImplementedBy(ServiceSplashImpl.class)
public interface ServiceSplash extends ServiceBase{
}
