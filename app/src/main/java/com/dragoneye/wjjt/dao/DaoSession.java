package com.dragoneye.wjjt.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.dragoneye.wjjt.dao.InvestRecord;
import com.dragoneye.wjjt.dao.EarningRecord;
import com.dragoneye.wjjt.dao.NewNormalActivity;
import com.dragoneye.wjjt.dao.NewPreferentialActivity;
import com.dragoneye.wjjt.dao.MessageBoxItem;

import com.dragoneye.wjjt.dao.InvestRecordDao;
import com.dragoneye.wjjt.dao.EarningRecordDao;
import com.dragoneye.wjjt.dao.NewNormalActivityDao;
import com.dragoneye.wjjt.dao.NewPreferentialActivityDao;
import com.dragoneye.wjjt.dao.MessageBoxItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig investRecordDaoConfig;
    private final DaoConfig earningRecordDaoConfig;
    private final DaoConfig newNormalActivityDaoConfig;
    private final DaoConfig newPreferentialActivityDaoConfig;
    private final DaoConfig messageBoxItemDaoConfig;

    private final InvestRecordDao investRecordDao;
    private final EarningRecordDao earningRecordDao;
    private final NewNormalActivityDao newNormalActivityDao;
    private final NewPreferentialActivityDao newPreferentialActivityDao;
    private final MessageBoxItemDao messageBoxItemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        investRecordDaoConfig = daoConfigMap.get(InvestRecordDao.class).clone();
        investRecordDaoConfig.initIdentityScope(type);

        earningRecordDaoConfig = daoConfigMap.get(EarningRecordDao.class).clone();
        earningRecordDaoConfig.initIdentityScope(type);

        newNormalActivityDaoConfig = daoConfigMap.get(NewNormalActivityDao.class).clone();
        newNormalActivityDaoConfig.initIdentityScope(type);

        newPreferentialActivityDaoConfig = daoConfigMap.get(NewPreferentialActivityDao.class).clone();
        newPreferentialActivityDaoConfig.initIdentityScope(type);

        messageBoxItemDaoConfig = daoConfigMap.get(MessageBoxItemDao.class).clone();
        messageBoxItemDaoConfig.initIdentityScope(type);

        investRecordDao = new InvestRecordDao(investRecordDaoConfig, this);
        earningRecordDao = new EarningRecordDao(earningRecordDaoConfig, this);
        newNormalActivityDao = new NewNormalActivityDao(newNormalActivityDaoConfig, this);
        newPreferentialActivityDao = new NewPreferentialActivityDao(newPreferentialActivityDaoConfig, this);
        messageBoxItemDao = new MessageBoxItemDao(messageBoxItemDaoConfig, this);

        registerDao(InvestRecord.class, investRecordDao);
        registerDao(EarningRecord.class, earningRecordDao);
        registerDao(NewNormalActivity.class, newNormalActivityDao);
        registerDao(NewPreferentialActivity.class, newPreferentialActivityDao);
        registerDao(MessageBoxItem.class, messageBoxItemDao);
    }
    
    public void clear() {
        investRecordDaoConfig.getIdentityScope().clear();
        earningRecordDaoConfig.getIdentityScope().clear();
        newNormalActivityDaoConfig.getIdentityScope().clear();
        newPreferentialActivityDaoConfig.getIdentityScope().clear();
        messageBoxItemDaoConfig.getIdentityScope().clear();
    }

    public InvestRecordDao getInvestRecordDao() {
        return investRecordDao;
    }

    public EarningRecordDao getEarningRecordDao() {
        return earningRecordDao;
    }

    public NewNormalActivityDao getNewNormalActivityDao() {
        return newNormalActivityDao;
    }

    public NewPreferentialActivityDao getNewPreferentialActivityDao() {
        return newPreferentialActivityDao;
    }

    public MessageBoxItemDao getMessageBoxItemDao() {
        return messageBoxItemDao;
    }

}
