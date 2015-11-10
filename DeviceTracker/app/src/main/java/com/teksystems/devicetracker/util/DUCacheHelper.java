package com.teksystems.devicetracker.util;

import com.parse.ParseObject;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.SettingEntity;

/**
 * Created by akokala on 22-9-15.
 */
public class DUCacheHelper {

    private static DUCacheHelper duCacheHelper;
    private SettingEntity mSettingEntity;
    private SessionsEntity mSessionsEntity;

    static {
        duCacheHelper = new DUCacheHelper();
    }

    /*Private constructor*/
    private DUCacheHelper() {
        mSettingEntity = new SettingEntity();
    }

    public SessionsEntity getSessionsEntity() {
        return mSessionsEntity;
    }

    public void setSessionsEntity(SessionsEntity sessionsEntity) {
        this.mSessionsEntity = sessionsEntity;
    }

    public SettingEntity getSettingEntity() {
        return mSettingEntity;
    }

    public void setSettingEntity(SettingEntity mSettingEntity) {
        this.mSettingEntity = mSettingEntity;
    }

    public static DUCacheHelper getDuCacheHelper() {
        return duCacheHelper;
    }
}
