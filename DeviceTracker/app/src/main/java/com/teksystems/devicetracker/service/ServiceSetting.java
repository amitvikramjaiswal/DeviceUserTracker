package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.teksystems.devicetracker.service.impl.ServiceSettingImpl;

/**
 * Created by akokala on 21-9-15.
 */
@ImplementedBy(ServiceSettingImpl.class)
public interface ServiceSetting extends ServiceBase {
}
