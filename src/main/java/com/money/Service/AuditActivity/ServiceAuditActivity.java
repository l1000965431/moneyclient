package com.money.Service.AuditActivity;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.auditActivityDAO.AuditActivityDao;
import com.money.model.ActivityDetailModel;
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
     * 设置项目审核状态
     * @param id
     * @param status
     * @return
     */
    public boolean setActivityAuditorResult(Long id, int status){
        ActivityVerifyModel ActivityVerifyModel = (ActivityVerifyModel) auditActivityDao.load(ActivityVerifyModel.class, id);
        if( ActivityVerifyModel == null ){
            return false;
        }

        ActivityVerifyModel.setAuditorStatus(status);
        auditActivityDao.update(ActivityVerifyModel);
        return true;
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
