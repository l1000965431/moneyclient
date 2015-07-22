package com.money.dao.auditActivityDAO;

import com.money.dao.BaseDao;
import com.money.model.ActivityVerifyModel;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Created by happysky on 15-7-22.
 * 审核项目dao
 */
@Repository
public class AuditActivityDao extends BaseDao {
    public ActivityVerifyModel getOldestActivity(){
        Session session = getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.asc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                .uniqueResult();

        session.flush();
        session.clear();
        session.close();
        return null;
    }

    public ActivityVerifyModel getNewestActivity(){
        Session session = getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.desc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                .uniqueResult();

        session.flush();
        session.clear();
        session.close();
        return null;
    }
}
