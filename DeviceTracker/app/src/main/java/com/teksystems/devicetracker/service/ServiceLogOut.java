package com.teksystems.devicetracker.service;

import com.google.inject.ImplementedBy;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseObject;
import com.teksystems.devicetracker.service.impl.ServiceLogOutImpl;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sprasar on 9/18/2015.
 */
@ImplementedBy(ServiceLogOutImpl.class)
public interface ServiceLogOut {
}
