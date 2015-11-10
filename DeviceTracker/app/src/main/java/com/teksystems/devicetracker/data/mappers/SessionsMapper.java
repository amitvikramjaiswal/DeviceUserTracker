package com.teksystems.devicetracker.data.mappers;

import com.parse.ParseObject;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by ajaiswal on 10/13/2015.
 */
public class SessionsMapper {

    public static ParseObject getParseObjectFromSessionsEntity(SessionsEntity sessionsEntity) {
        if (sessionsEntity == null)
            return null;

        ParseObject parseObject = new ParseObject(Utility.SESSIONS);

        if (sessionsEntity.getObjectId() != null)
            parseObject.setObjectId(sessionsEntity.getObjectId());
        if (sessionsEntity.getLoginTime() != null)
            parseObject.put(Utility.LOGIN_TIME, sessionsEntity.getLoginTime());
        if (sessionsEntity.getLogoutTime() != null)
            parseObject.put(Utility.LOGOUT_TIME, sessionsEntity.getLogoutTime());
        if (sessionsEntity.getUserEntity() != null)
            parseObject.put(Utility.USER_POINTER, UserMapper.getParseObjectFromUserEntity(sessionsEntity.getUserEntity()));
        if (sessionsEntity.getDeviceEntity() != null)
            parseObject.put(Utility.DEVICE_POINTER, DeviceMapper.getParseObjectFromDeviceEntity(sessionsEntity.getDeviceEntity()));
        if (sessionsEntity.getCreatedAt() != null)
            parseObject.put(Utility.CREATED_AT, sessionsEntity.getCreatedAt());
        if (sessionsEntity.getUpdatedAt() != null)
            parseObject.put(Utility.UPDATED_AT, sessionsEntity.getUpdatedAt());
        parseObject.put(Utility.LOGGED_IN, sessionsEntity.isLoggedIn());

        return parseObject;
    }

    public static SessionsEntity getSessionsEntityFromParseObject(ParseObject parseObject) {
        if (parseObject == null)
            return null;

        SessionsEntity sessionsEntity = new SessionsEntity();

        sessionsEntity.setObjectId(parseObject.getObjectId());
        sessionsEntity.setLoginTime(parseObject.has(Utility.LOGIN_TIME) ? parseObject.getDate(Utility.LOGIN_TIME) : null);
        sessionsEntity.setLogoutTime(parseObject.has(Utility.LOGOUT_TIME) ? parseObject.getDate(Utility.LOGOUT_TIME) : null);
        sessionsEntity.setLoggedIn(parseObject.has(Utility.LOGGED_IN) ? parseObject.getBoolean(Utility.LOGGED_IN) : false);
        sessionsEntity.setUserEntity(parseObject.has(Utility.USER_POINTER) ? UserMapper.getUserEntityFromParseObject(parseObject.getParseObject(Utility.USER_POINTER)) : null);
        sessionsEntity.setDeviceEntity(parseObject.has(Utility.DEVICE_POINTER) ? DeviceMapper.getDeviceEntityFromParseObject(parseObject.getParseObject(Utility.DEVICE_POINTER)) : null);
        sessionsEntity.setCreatedAt(parseObject.getCreatedAt());
        sessionsEntity.setUpdatedAt(parseObject.getUpdatedAt());

        return sessionsEntity;
    }

}
