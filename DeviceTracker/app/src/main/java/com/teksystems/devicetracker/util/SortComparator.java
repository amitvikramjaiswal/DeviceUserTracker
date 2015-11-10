package com.teksystems.devicetracker.util;

import com.teksystems.devicetracker.data.entities.BaseEntity;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;

import java.util.Comparator;

/**
 * Created by ajaiswal on 9/30/2015.
 */
public class SortComparator implements Comparator<BaseEntity> {
    @Override
    public int compare(BaseEntity lhs, BaseEntity rhs) {

        if (lhs instanceof UserEntity && rhs instanceof UserEntity) {
            UserEntity lhsUserEntity = (UserEntity) lhs;
            UserEntity rhsUserEntity = (UserEntity) rhs;
            return lhsUserEntity.getUsername().compareToIgnoreCase(rhsUserEntity.getUsername());
        }
        else if (lhs instanceof DeviceEntity && rhs instanceof DeviceEntity) {
            DeviceEntity lhsDeviceEntity = (DeviceEntity) lhs;
            DeviceEntity rhsDeviceEntity = (DeviceEntity) rhs;
            return lhsDeviceEntity.getDeviceName().compareToIgnoreCase(rhsDeviceEntity.getDeviceName());
        }
        else if (lhs instanceof ProjectEntity && rhs instanceof ProjectEntity) {
            ProjectEntity lhsProjectEntity = (ProjectEntity) lhs;
            ProjectEntity rhsProjectEntity = (ProjectEntity) rhs;
            return lhsProjectEntity.getProjectName().compareToIgnoreCase(rhsProjectEntity.getProjectName());
        }
        return 0;
    }
}
