package com.teksystems.devicetracker.data.entities;

import java.util.Date;
import java.util.List;

/**
 * Created by ajaiswal on 9/22/2015.
 */
public class DeviceEntity extends BaseEntity {
    private String objectId;
    private String deviceOS;
    private String macAddress;
    private String deviceName;
    private UserEntity userEntity;
    private ProjectEntity projectEntity;
    private SessionsEntity sessionsEntity;
    private List<Object> accessories;
    private String notes;
    private String deviceVersion;
    private boolean withAdmin;
    private boolean isEnabled;
    private Date createdAt;
    private Date updatedAt;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public SessionsEntity getSessionsEntity() {
        return sessionsEntity;
    }

    public void setSessionsEntity(SessionsEntity sessionsEntity) {
        this.sessionsEntity = sessionsEntity;
    }

    public List<Object> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<Object> accessories) {
        this.accessories = accessories;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public boolean isWithAdmin() {
        return withAdmin;
    }

    public void setWithAdmin(boolean withAdmin) {
        this.withAdmin = withAdmin;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
