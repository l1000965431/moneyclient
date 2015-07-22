package com.money.Service.AuditActivity;

import com.money.Service.ServiceBase;
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
public class ServiceAuditActivity extends ServiceBase {
    @Autowired
    private GeneraDAO generaDAO;

    @SuppressWarnings("unchecked")
    public ActivityVerifyModel getOldestActivity(){
        Session session = generaDAO.getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.asc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_UN_AUDITOR))
                .uniqueResult();

        session.close();
        return activityVerifyModel;
    }

    public ActivityVerifyModel getNewestActivity(){
        Session session = generaDAO.getNewSession();
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(1)
                .addOrder(Order.desc("id"))
                .add(Restrictions.eq("auditorStatus", ActivityVerifyModel.STATUS_UN_AUDITOR))
                .uniqueResult();

        session.close();
        return activityVerifyModel;
    }

    /**
     * 获取项目列表
     * @param isAsc 排列方式是升序还是降序
     * @param activityStatus {@link ActivityVerifyModel#STATUS_UN_AUDITOR}
     *                      {@link ActivityVerifyModel#STATUS_AUDITOR_PASS}
     *                      {@link ActivityVerifyModel#STATUS_AUDITOR_NOT_PASS}
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getActivityList(boolean isAsc, int activityStatus){
        Session session = generaDAO.getNewSession();
        List<ActivityVerifyModel> list = session.createCriteria(ActivityVerifyModel.class)
                .setMaxResults(10)
                .addOrder(Order.desc("id"))
                .add(Restrictions.eq("auditorStatus", activityStatus))
                .list();
        return list;
    }

    /**
     * 获得项目审核状态
     * @param id
     * @return {@link ActivityVerifyModel#STATUS_UN_AUDITOR}
     *         或者-1:没有此项目
     *
     */
    public int getActivityStatus(Long id){
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) generaDAO.load(ActivityVerifyModel.class, id);
        if( activityVerifyModel == null ){
            return -1;
        }
        return activityVerifyModel.getAuditorStatus();
    }

    /**
     * 设置项目审核状态
     * @param id
     * @param status
     * @return
     */
    public boolean setActivityAuditorResult(Long id, int status){
        ActivityVerifyModel ActivityVerifyModel = (ActivityVerifyModel) generaDAO.load(ActivityVerifyModel.class, id);
        if( ActivityVerifyModel == null ){
            return false;
        }

        ActivityVerifyModel.setAuditorStatus(status);
        generaDAO.update(ActivityVerifyModel);
        return true;
    }

    public boolean setActivityToGroup(){
        generaDAO.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(Session session) throws Exception {
                ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) session.get(ActivityVerifyModel.class, 4l);
                if (activityVerifyModel != null) {
                    ActivityDetailModel activityDetailModel = verifyToDetail(activityVerifyModel);
                    session.save(activityDetailModel);
                    session.delete(activityVerifyModel);
                }
            }
        });
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
