package com.money.Service.activity;

import com.money.Service.Lottery.LotteryService;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.google.gson.reflect.TypeToken;
import com.money.Service.Ticket.TicketService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.List;
import java.util.Map;

/**
 * 项目服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Service("ActivityService")
public class ActivityService extends ServiceBase implements ServiceInterface {

    @Autowired
    private activityDAO activityDao;

    @Autowired
    private PurchaseInAdvance purchaseInAdvance;

    @Autowired
    private TicketService ticketService;

    @Autowired
    LotteryService lotteryService;

    public ActivityDetailModel getActivityDetails(String InstallmentActivityID) {
        ActivityDetailModel activitymodel = activityDao.getActivityDetails(InstallmentActivityID);
        return activitymodel;
    }

    public ActivityDynamicModel getActivityDynamic(String InstallmentActivityID) {
        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamic(InstallmentActivityID);
        return activityDynamicModel;
    }

    public ActivityDetailModel getActivityDetailsNoTran(String InstallmentActivityID) {
        ActivityDetailModel activitymodel = activityDao.getActivityDetaillNoTransaction(InstallmentActivityID);
        return activitymodel;
    }

    public ActivityDynamicModel getActivityDynamicNoTran(String InstallmentActivityID) {
        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);
        return activityDynamicModel;
    }


    @SuppressWarnings("unchecked")
    public List<ActivityDetailModel> getAllActivityDetail(int pageIndex, int numPerPage) {
        return activityDao.getActivityListActivity(pageIndex, numPerPage);
    }

    /**
     * 设置项目的当前金额 需要手动提交
     *
     * @param addlines 投资金额
     * @return
     */
    public void SetActivityCurLines(ActivityDynamicModel activitydynamicmodel, int addlines) {
        int activitycurlines = activitydynamicmodel.getActivityState();
        activitydynamicmodel.setActivityCurLines(activitycurlines + addlines);
    }


    /**
     * 检测分期项目是否完成并设置项目完成
     *
     * @return
     */
    private boolean IsActivityComplete(ActivityDynamicModel activitydynamicmodel) {
        int curactivitylines = activitydynamicmodel.getActivityCurLines();
        int totalactivitylines = activitydynamicmodel.getActivityTotalAmount();

        if (curactivitylines >= totalactivitylines) {
            activitydynamicmodel.setActivityState(ActivityDynamicModel.ONLINE_ACTIVITY_COMPLETE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消项目
     *
     * @param activityID
     * @return
     */
    public String CanelActivity(final String activityID) {
        if (activityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {

                ActivityDetailModel activityDetailModel = activityDao.getActivityDetails(activityID);
                activityDetailModel.setStatus(ActivityDetailModel.ONLINE_ACTIVITY_FAILED);
                activityDao.update(activityDetailModel);

                ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
                activityDao.delete(activitydynamicmodel);
            }
        }) == Config.SERVICE_SUCCESS) {
            return Config.SERVICE_SUCCESS;
        } else {
            return Config.SERVICE_FAILED;
        }
    }


    /**
     * 用户已经收益的项目
     *
     * @param UserID
     * @return
     */
    public List GetActivityHasEarnings(String UserID) {

        return null;
    }

    /**
     * 设置项目状态
     *
     * @param InstallmentActivityID
     * @param Status
     */
    public void SetInstallmentActivityStatus(String InstallmentActivityID, final int Status) {
        final ActivityDetailModel activityDetailModel = activityDao.getActivityDetails(InstallmentActivityID);
        final ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamic(InstallmentActivityID);

        if (activityDetailModel == null || activityDynamicModel == null) {
            return;
        }

        activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                activityDetailModel.setStatus(Status);
                activityDynamicModel.setActivityState(Status);

                session.update(activityDetailModel);
                session.update(activityDynamicModel);

                return true;
            }
        });

    }

    /**
     * 设置项目状态
     *
     * @param ActivityID
     * @param Status
     */
    public void SetActivityStatus(String ActivityID, final int Status) {
        final ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDao.getActivityVerifyCompleteModel(ActivityID);

        if (activityVerifyCompleteModel == null) {
            return;
        }

        activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                activityVerifyCompleteModel.setStatus(Status);
                session.update(activityVerifyCompleteModel);

                return true;
            }
        });
    }


    /**
     * 分期的项目开始
     *
     * @param ActivityID  项目ID
     * @param Installment 第几期
     * @return
     */

    public boolean InstallmentActivityIDStart(String ActivityID, int Installment) {
        String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);

        //创建分期项目票表
        activityDao.CreateTicketDB(InstallmentActivityID);

        //创建分期项目票ID
        ActivityCreateTicketID( InstallmentActivityID );

        //预购项目
        purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance(ActivityID, InstallmentActivityID);

        //设置项目开始
        SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_START);

        return true;
    }


    /**
     * 总项目项目开始
     *
     * @param ActivityID 项目ID
     * @return
     */
    public boolean ActivityCompleteStart(String ActivityID) {
        //创建预购项目表
        activityDao.CreatePurchaseInAdvanceDB(ActivityID);

        //设置项目开始
        SetActivityStatus(ActivityID, ActivityDetailModel.ONLINE_ACTIVITY_START);
        //设置第一期开始
        InstallmentActivityIDStart(ActivityID, 1);
        return true;
    }


    /**
     * 创建分期项目票ID
     *
     * @param InstallmentActivityID
     */
    public void ActivityCreateTicketID(String InstallmentActivityID) {
        ActivityDetailModel activityDetailModel = getActivityDetails(InstallmentActivityID);

        if (activityDetailModel == null) {
            return;
        }

        if( activityDetailModel.getStatus() != ActivityDetailModel.ONLINE_ACTIVITY_NOSTART ){
            return;
        }

        int TotalLines = activityDetailModel.getTargetFund();

        ticketService.CreateTickID(InstallmentActivityID, TotalLines);
    }


    /**
     * 设置项目完成
     * @param ActivityID
     */
    public void SetActivityEnd( String ActivityID ){
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDao.getActivityVerifyCompleteModel( ActivityID );
        if( activityVerifyCompleteModel.IsEnoughInstallmentNum() ){
            SetActivityStatus(ActivityID, ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);
        }
    }

    /**
     * 设置分期项目完成
     * @param InstallmentActivityID
     */
    public void SetInstallmentActivityEnd( String InstallmentActivityID ){

        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamic( InstallmentActivityID );
        if(activityDynamicModel.IsEnough() ){
            SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);

            ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

            int CurInstallmentNum = activityVerifyCompleteModel.getCurInstallmentNum();
            CurInstallmentNum++;
            activityVerifyCompleteModel.setCurInstallmentNum(CurInstallmentNum);
            activityDao.update( activityVerifyCompleteModel );

            //发奖
            lotteryService.StartLottery( InstallmentActivityID );

            //如果所有分期项目完成  设置父项目完成
            SetActivityEnd( activityDynamicModel.getActivityVerifyCompleteModel().getActivityId() );
        }
    }

    /**
     * 获取项目详情
     * @param activityId
     * @return
     */
    public ActivityVerifyCompleteModel getActivityInformation(String activityId){
        return (ActivityVerifyCompleteModel)activityDao.load(ActivityVerifyCompleteModel.class, activityId);
    }

    /**
     * 获取项目投资详情（领投金额、收益层次等)
     * @param activityStageId
     * @return
     */
    public ActivityDetailModel getActivityInvestInfo(String activityStageId){
        return activityDao.getActivityInvestInfo(activityStageId);
    }

}
