package com.teksystems.devicetracker.data.mappers;

import com.parse.ParseObject;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by ajaiswal on 10/1/2015.
 */
public class UserMapper {

    public static ParseObject getParseObjectFromUserEntity(UserEntity userEntity) {
        if (userEntity == null)
            return null;

        ParseObject parseObject = new ParseObject(Utility.USER_TABLE);

        if (userEntity.getObjectId() != null)
            parseObject.setObjectId(userEntity.getObjectId());
        if (userEntity.getName() != null)
            parseObject.put(Utility.NAME, userEntity.getName());
        if (userEntity.getUsername() != null)
            parseObject.put(Utility.USERNAME, userEntity.getUsername());
        if (userEntity.getPassword() != null)
            parseObject.put(Utility.PASSWORD, userEntity.getPassword());
        parseObject.put(Utility.IS_ADMIN, userEntity.isAdmin());
        parseObject.put(Utility.ATTEMPT, userEntity.getAttempt());
        parseObject.put(Utility.IS_ENABLED, userEntity.isEnabled());
        if (userEntity.getCreatedAt() != null)
            parseObject.put(Utility.CREATED_AT, userEntity.getCreatedAt());
        if (userEntity.getUpdatedAt() != null)
            parseObject.put(Utility.UPDATED_AT, userEntity.getUpdatedAt());

        return parseObject;
    }

    public static UserEntity getUserEntityFromParseObject(ParseObject parseObject) {
        if (parseObject == null)
            return null;

        UserEntity userEntity = new UserEntity();

        userEntity.setObjectId(parseObject.getObjectId());
        userEntity.setName(parseObject.has(Utility.NAME) ? parseObject.getString(Utility.NAME) : null);
        userEntity.setUsername(parseObject.has(Utility.USERNAME) ? parseObject.getString(Utility.USERNAME) : null);
        userEntity.setPassword(parseObject.has(Utility.PASSWORD) ? parseObject.getString(Utility.PASSWORD) : null);
        userEntity.setIsAdmin(parseObject.has(Utility.IS_ADMIN) ? parseObject.getBoolean(Utility.IS_ADMIN) : false);
        userEntity.setAttempt(parseObject.has(Utility.ATTEMPT) ? parseObject.getInt(Utility.ATTEMPT) : 0);
        userEntity.setIsEnabled(parseObject.has(Utility.IS_ENABLED) ? parseObject.getBoolean(Utility.IS_ENABLED) : false);
        userEntity.setCreatedAt(parseObject.getCreatedAt());
        userEntity.setUpdatedAt(parseObject.getUpdatedAt());

        return userEntity;
    }

}
