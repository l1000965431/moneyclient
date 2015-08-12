package com.money.Service.AuditActivity;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.auditActivityDAO.AuditActivityDao;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.ActivityVerifyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by happysky on 15-7-11.
 */

@Service("ServiceAuditActivity")
public class ServiceAuditActivity extends ServiceBase implements ServiceInterface {
    @Autowired
    private AuditActivityDao auditActivityDao;

    @SuppressWarnings("unchecked")
    public ActivityVerifyModel getOldestActivity(){
        return auditActivityDao.getNewestActivity();
    }

    public ActivityVerifyModel getNewestActivity(){
        return auditActivityDao.getOldestActivity();
    }

    @SuppressWarnings("unchecked")
    public List<ActivityVerifyModel> getActivityList(boolean isAsc){
        return auditActivityDao.getAllList(ActivityVerifyModel.class, "id", isAsc);
    }

    /**
     * 获得所有审核中项目
     * @return
     */
    public List<ActivityVerifyModel> getAuditingActivityList(){
        return auditActivityDao.getAuditingActivityList();
    }

    public List<ActivityVerifyModel> getAuditingActivityList(int pageIndex, int pageNum){
        return auditActivityDao.getActivityList(ActivityVerifyModel.STATUS_FIRST_AUDITING, pageIndex, pageNum);
    }

    public List<ActivityVerifyModel> getNotPassedActivityList(int pageIndex, int pageNum){
        return auditActivityDao.getActivityList(ActivityVerifyModel.STATUS_AUDITOR_NOT_PASS, pageIndex, pageNum);
    }

    /**
     * 设置项目审核状态
     * @param id
     * @param status
     * @return
     */
    public boolean setActivityAuditorResult(Long id, int status, String param){
        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) auditActivityDao.load(ActivityVerifyModel.class, id);
        if( activityVerifyModel == null ){
            return false;
        }

        switch (status){
            case ActivityVerifyModel.STATUS_NEED_REVAMP:
                setActivityNeedRevamp(activityVerifyModel, param);
                break;
            case ActivityVerifyModel.STATUS_AUDITOR_NOT_PASS:
                setActivityNotPass(activityVerifyModel);
                break;
            case ActivityVerifyModel.STATUS_AUDITOR_PASS:
                return setActivityPass(activityVerifyModel);
            default:
                activityVerifyModel.setAuditorStatus(status);
                auditActivityDao.update(activityVerifyModel);
                break;
        }

        return true;
    }

    /**
     * 设置项目审核不通过
     * @return
     */
    public void setActivityNotPass(ActivityVerifyModel activityVerifyModel){
        activityVerifyModel.setAuditorStatus(ActivityVerifyModel.STATUS_AUDITOR_NOT_PASS);

        auditActivityDao.update(activityVerifyModel);
    }

    /**
     * 设置项目审核不通过，需要修改
     * @return
     */
    public void setActivityNeedRevamp(ActivityVerifyModel activityVerifyModel, String reason){
        activityVerifyModel.setAuditorStatus(ActivityVerifyModel.STATUS_NEED_REVAMP);
        activityVerifyModel.setRevampCount(activityVerifyModel.getRevampCount() + 1);
        auditActivityDao.update(activityVerifyModel);
    }

    /**
     * 设置项目通过
     * @param activityVerifyModel
     */
    public boolean setActivityPass(ActivityVerifyModel activityVerifyModel){
        activityVerifyModel.setAuditorStatus(ActivityVerifyModel.STATUS_AUDITOR_PASS);
        ActivityVerifyCompleteModel completeModel = verifyComplete(activityVerifyModel);
        return auditActivityDao.setActivityPass(activityVerifyModel, completeModel);
    }

    public boolean setActivityToGroup(){
        ActivityDetailModel activityDetailModel = (ActivityDetailModel) auditActivityDao.load(ActivityDetailModel.class, 1l);
        auditActivityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao baseDao) throws Exception {
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


    private ActivityVerifyCompleteModel verifyComplete(ActivityVerifyModel activity){
        ActivityVerifyCompleteModel completeModel = new ActivityVerifyCompleteModel();

        completeModel.setActivityId(String.valueOf(activity.getId()));
        completeModel.setActivityIntroduce(activity.getActivityIntroduce());
        completeModel.setAddress(activity.getAddress());
        completeModel.setCategory(activity.getCategory());
        completeModel.setImageUrl(activity.getImageUrl());
        completeModel.setMarketAnalysis(activity.getMarketAnalysis());
        completeModel.setName(activity.getName());
        completeModel.setRaiseDay(activity.getRaiseDay());
        completeModel.setSummary(activity.getSummary());
        completeModel.setTags(activity.getTags());
        completeModel.setTargetFund(activity.getTargetFund());
        completeModel.setTeamIntroduce(activity.getTeamIntroduce());
        completeModel.setTeamSize(activity.getTeamSize());
        completeModel.setVideoUrl(activity.getVideoUrl());

        return completeModel;
    }
}
