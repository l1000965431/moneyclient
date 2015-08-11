package com.dragoneye.wjjt.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig projectDaoConfig;
    private final DaoConfig projectImageDaoConfig;

    private final ProjectDao projectDao;
    private final ProjectImageDao projectImageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        projectDaoConfig = daoConfigMap.get(ProjectDao.class).clone();
        projectDaoConfig.initIdentityScope(type);

        projectImageDaoConfig = daoConfigMap.get(ProjectImageDao.class).clone();
        projectImageDaoConfig.initIdentityScope(type);


        projectDao = new ProjectDao(projectDaoConfig, this);
        projectImageDao = new ProjectImageDao(projectImageDaoConfig, this);

        registerDao(Project.class, projectDao);
        registerDao(ProjectImage.class, projectImageDao);
    }
    
    public void clear() {
        projectDaoConfig.getIdentityScope().clear();
        projectImageDaoConfig.getIdentityScope().clear();
    }

    public ProjectDao getProjectDao() {
        return projectDao;
    }

    public ProjectImageDao getProjectImageDao() {
        return projectImageDao;
    }
}
