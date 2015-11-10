package com.teksystems.devicetracker.data.mappers;

import com.parse.ParseObject;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.util.Utility;

/**
 * Created by ajaiswal on 10/6/2015.
 */
public class ProjectMapper {

    public static ParseObject getParseObjectFromProjectEntity(ProjectEntity projectEntity) {
        if (projectEntity == null)
            return null;
        ParseObject parseObject = new ParseObject(Utility.PROJECT);

        if (projectEntity.getObjectId() != null)
            parseObject.setObjectId(projectEntity.getObjectId());
        if (projectEntity.getProjectName() != null)
            parseObject.put(Utility.PROJECT_NAME, projectEntity.getProjectName());
        if (projectEntity.getProjectDescription() != null)
            parseObject.put(Utility.PROJECT_DESCRIPTION, projectEntity.getProjectDescription());
        if (projectEntity.getCreatedAt() != null)
            parseObject.put(Utility.CREATED_AT, projectEntity.getCreatedAt());
        if (projectEntity.getUpdatedAt() != null)
            parseObject.put(Utility.UPDATED_AT, projectEntity.getUpdatedAt());

        return parseObject;
    }

    public static ProjectEntity getProjectEntityFromParseObject(ParseObject parseObject) {
        if (parseObject == null)
            return null;
        ProjectEntity projectEntity = new ProjectEntity();

        projectEntity.setObjectId(parseObject.getObjectId());
        projectEntity.setProjectName(parseObject.has(Utility.PROJECT_NAME) ? parseObject.getString(Utility.PROJECT_NAME) : null);
        projectEntity.setProjectDescription(parseObject.has(Utility.PROJECT_DESCRIPTION) ? parseObject.getString(Utility.PROJECT_DESCRIPTION) : null);
        projectEntity.setCreatedAt(parseObject.getCreatedAt());
        projectEntity.setUpdatedAt(parseObject.getUpdatedAt());

        return projectEntity;
    }

}
