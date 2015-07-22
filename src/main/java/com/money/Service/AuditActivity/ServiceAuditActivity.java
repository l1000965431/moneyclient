package com.money.Service.AuditActivity;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.BaseDao;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionCallback;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityVerifyModel;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by happysky on 15-7-11.
 */

@Service("ServiceAuditActivity")
public class ServiceAuditActivity extends ServiceBase implements ServiceInterface {
    @Autowired
    private GeneraDAO baseDao;

    @SuppressWarnings("unchecked")
    public ActivityVerifyModel getOldestActivity(){
/*        Session session = baseDao.getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.asc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                .uniqueResult();

        session.flush();
        session.clear();
        session.close();*/
        return null;
    }

    public ActivityVerifyModel getNewestActivity(){
/*        Session session = baseDao.getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.desc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_FIRST_AUDITING))
                .uniqueResult();

        session.flush();
        session.clear();
        session.close();*/
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getActivityList(boolean isAsc){
        return baseDao.getAllList(ActivityVerifyModel.class, "id", isAsc);
    }

    /**
     * 设置项目审核状态
     * @param id
     * @param status
     * @return
     */
    public boolean setActivityAuditorResult(Long id, int status){
        ActivityVerifyModel ActivityVerifyModel = (ActivityVerifyModel)baseDao.load(ActivityVerifyModel.class, id);
        if( ActivityVerifyModel == null ){
            return false;
        }

        ActivityVerifyModel.setAuditorStatus(status);
        baseDao.update(ActivityVerifyModel);
        return true;
    }

    public boolean setActivityToGroup(){
        ActivityDetailModel activityDetailModel = (ActivityDetailModel)baseDao.load(ActivityDetailModel.class, 1l);
        baseDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao baseDao ) throws Exception {
/*                ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.get(ActivityVerifyModel.class, 4l);
                if (activityVerifyModel != null) {
                    ActivityDetailModel activityDetailModel = verifyToDetail(activityVerifyModel);
                    session.save(activityDetailModel);
                    session.delete(activityVerifyModel);*/
                }
            //}
        });
//        Session session = baseDao.getNewSession();
//        Transaction transaction = session.beginTransaction();
//
//        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.get(ActivityVerifyModel.class, 1l);
//        if( activityVerifyModel != null ){
//            ActivityDetailModel activityDetailModel = verifyToDetail(activityVerifyModel);
//            session.save(activityDetailModel);
//            session.delete(activityVerifyModel);
//        }
//
//        transaction.commit();
//
//        session.flush();
//        session.clear();
//        session.close();
        return true;
    }

    private ActivityDetailModel verifyToDetail(ActivityVerifyModel activity){
        ActivityDetailModel activityDetailModel = new ActivityDetailModel();

        activityDetailModel.setActivityIntroduce(activity.getActivityIntroduce());
        activityDetailModel.setAddress(activity.getAddress());
        activityDetailModel.setCategory(activity.getCategory());
        activityDetailModel.setImageUrl(activity.getImageUrl());
        activityDetailModel.setMarketAnalysis(activity.getMarketAnalysis());
        activityDetailModel.setName(activity.getName());
        activityDetailModel.setRaiseDay(activity.getRaiseDay());
        activityDetailModel.setSummary(activity.getSummary());
        activityDetailModel.setTags(activity.getTags());
        activityDetailModel.setTargetFund(activity.getTargetFund());
        activityDetailModel.setTeamIntroduce(activity.getTeamIntroduce());
        activityDetailModel.setTeamSize(activity.getTeamSize());
        activityDetailModel.setVideoUrl(activity.getVideoUrl());

        return activityDetailModel;
    }
}
