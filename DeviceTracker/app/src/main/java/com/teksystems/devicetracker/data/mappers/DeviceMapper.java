package com.teksystems.devicetracker.data.mappers;

import com.parse.ParseObject;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by ajaiswal on 10/6/2015.
 */
public class DeviceMapper {

    public static ParseObject getParseObjectFromDeviceEntity(DeviceEntity deviceEntity) {
        if (deviceEntity == null) {
            return null;
        }

        ParseObject parseObject = new ParseObject(Utility.DEVICE);

        if (deviceEntity.getObjectId() != null) {
            parseObject.setObjectId(deviceEntity.getObjectId());
        }
        if (deviceEntity.getDeviceName() != null) {
            parseObject.put(Utility.DEVICE_NAME, deviceEntity.getDeviceName());
        }
        if (deviceEntity.getDeviceOS() != null) {
            parseObject.put(Utility.DEVICE_OS, deviceEntity.getDeviceOS());
        }
        if (deviceEntity.getMacAddress() != null) {
            parseObject.put(Utility.MAC_ADDRESS, deviceEntity.getMacAddress());
        }
        ParseObject taggedUser = UserMapper.getParseObjectFromUserEntity(deviceEntity.getUserEntity());
        if (taggedUser != null) {
            parseObject.put(Utility.TAGGED_USER, taggedUser);
        }
        if (deviceEntity.getAccessories() != null) {
            parseObject.put(Utility.ACCESSORIES, deviceEntity.getAccessories());
        }
        ParseObject taggedProject = ProjectMapper.getParseObjectFromProjectEntity(deviceEntity.getProjectEntity());
        if (taggedProject != null) {
            parseObject.put(Utility.TAGGED_PROJECT, taggedProject);
        }
        ParseObject lastActiveSession = SessionsMapper.getParseObjectFromSessionsEntity(deviceEntity.getSessionsEntity());
        if (lastActiveSession != null) {
            parseObject.put(Utility.LAST_ACTIVE_SESSION, lastActiveSession);
        }
        if (deviceEntity.getNotes() != null) {
            parseObject.put(Utility.NOTES, deviceEntity.getNotes());
        }
        if (deviceEntity.getDeviceVersion() != null) {
            parseObject.put(Utility.DEVICE_VERSION, deviceEntity.getDeviceVersion());
        }
        if (deviceEntity.getCreatedAt() != null) {
            parseObject.put(Utility.CREATED_AT, deviceEntity.getCreatedAt());
        }
        parseObject.put(Utility.IS_ENABLED, deviceEntity.isEnabled());
        if (deviceEntity.getUpdatedAt() != null) {
            parseObject.put(Utility.UPDATED_AT, deviceEntity.getUpdatedAt());
        }

        return parseObject;
    }

    public static DeviceEntity getDeviceEntityFromParseObject(ParseObject parseObject) {
        if (parseObject == null) {
            return null;
        }

        DeviceEntity deviceEntity = new DeviceEntity();

        deviceEntity.setObjectId(parseObject.getObjectId());
        deviceEntity.setDeviceName(parseObject.has(Utility.DEVICE_NAME) ? parseObject.getString(Utility.DEVICE_NAME) : null);
        deviceEntity.setDeviceOS(parseObject.has(Utility.DEVICE_OS) ? parseObject.getString(Utility.DEVICE_OS) : null);
        deviceEntity.setMacAddress(parseObject.has(Utility.MAC_ADDRESS) ? parseObject.getString(Utility.MAC_ADDRESS) : null);
        deviceEntity.setUserEntity(UserMapper.getUserEntityFromParseObject(parseObject.has(Utility.TAGGED_USER) ? parseObject.getParseObject(Utility.TAGGED_USER) : null));
        deviceEntity.setAccessories(parseObject.has(Utility.ACCESSORIES) ? parseObject.getList(Utility.ACCESSORIES) : null);
        deviceEntity.setDeviceVersion(parseObject.has(Utility.DEVICE_VERSION) ? parseObject.getString(Utility.DEVICE_VERSION) : null);
        deviceEntity.setNotes(parseObject.has(Utility.NOTES) ? parseObject.getString(Utility.NOTES) : null);
        deviceEntity.setProjectEntity(ProjectMapper.getProjectEntityFromParseObject(parseObject.has(Utility.TAGGED_PROJECT) ? parseObject.getParseObject(Utility.TAGGED_PROJECT) : null));
        deviceEntity.setSessionsEntity(SessionsMapper.getSessionsEntityFromParseObject(parseObject.has(Utility.LAST_ACTIVE_SESSION) ? parseObject.getParseObject(Utility.LAST_ACTIVE_SESSION) : null));
        deviceEntity.setIsEnabled(parseObject.has(Utility.IS_ENABLED) ? parseObject.getBoolean(Utility.IS_ENABLED) : false);
        deviceEntity.setCreatedAt(parseObject.getCreatedAt());
        deviceEntity.setUpdatedAt(parseObject.getUpdatedAt());

        return deviceEntity;
    }

}
