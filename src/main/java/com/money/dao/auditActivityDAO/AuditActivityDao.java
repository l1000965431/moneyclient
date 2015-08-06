package com.money.dao.auditActivityDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.TransactionSessionCallback;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.ActivityVerifyModel;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public boolean setActivityPass(final ActivityVerifyModel verifyModel, final ActivityVerifyCompleteModel completeModel){
        String result = excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                //session.delete(verifyModel);
                session.update(verifyModel);
                session.save(completeModel);
                return true;
            }
        });

        return result.compareTo(Config.SERVICE_SUCCESS) == 0;
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getAuditingActivityList(){
        Session session = getNewSession();
        List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyModel.class)
                .addOrder(Order.desc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                .list();

        session.flush();
        session.clear();
        session.close();
        return activityVerifyModels;
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getActivityList(int status, int pageIndex, int pageNum){
        Session session = getNewSession();
        List<ActivityVerifyModel> activityVerifyModels = session.createCriteria(ActivityVerifyModel.class)
                .addOrder(Order.asc("id"))
                .add(Restrictions.eq("auditorStatus", status))
                .setFirstResult(pageIndex * pageNum)
                .setMaxResults(pageNum)
                .list();

        return activityVerifyModels;
    }
}
