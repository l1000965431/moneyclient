package com.money.Service.activity;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.Lottery.LotteryService;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.google.gson.reflect.TypeToken;
import com.money.Service.Ticket.TicketService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.config.ServerReturnValue;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.ArrayList;
import java.util.HashMap;
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
        final ActivityDetailModel activityDetailModel = activityDao.getActivityDetaillNoTransaction(InstallmentActivityID);
        final ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);

        if (activityDetailModel == null || activityDynamicModel == null) {
            return;
        }
        activityDetailModel.setStatus(Status);
        activityDynamicModel.setActivityState(Status);

        activityDao.updateNoTransaction(activityDetailModel);
        activityDao.updateNoTransaction(activityDynamicModel);
    }

    /**
     * 设置项目状态
     *
     * @param ActivityID
     * @param Status
     */
    public void SetActivityStatus(String ActivityID, final int Status) {
        final ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDao.getActivityVerifyCompleteModelNoTransaction(ActivityID);

        if (activityVerifyCompleteModel == null) {
            return;
        }

        activityVerifyCompleteModel.setStatus(Status);
        activityDao.updateNoTransaction(activityVerifyCompleteModel);

    }


    /**
     * 分期的项目开始
     *
     * @param ActivityID  项目ID
     * @param Installment 第几期
     * @return
     */

    private boolean InstallmentActivityIDStart(String ActivityID, int Installment) throws Exception {
        String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
        //创建分期项目票表
        activityDao.CreateTicketDB(InstallmentActivityID);
        //创建分期项目票ID
        ActivityCreateTicketID(InstallmentActivityID);
        //预购项目
        purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance(ActivityID, InstallmentActivityID);
        //设置项目开始
        SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_START);

        return true;
    }


    public void InstallmentActivityStart(final String ActivityID, final int Installment) throws Exception {
        activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                return InstallmentActivityIDStart(ActivityID, Installment);
            }
        });

        //预购项目已经完成  开始下一期
        String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
        SetInstallmentActivityEnd(InstallmentActivityID);
    }


    /**
     * 总项目项目开始
     *
     * @param ActivityID 项目ID
     * @return
     */
    public boolean ActivityCompleteStart(final String ActivityID) {
        activityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                //创建预购项目表
                activityDao.CreatePurchaseInAdvanceDB(ActivityID);
                //设置项目开始
                SetActivityStatus(ActivityID, ActivityVerifyModel.STATUS_START_RAISE);
                //设置第一期开始
                InstallmentActivityIDStart(ActivityID, 1);
                return;
            }
        });
        return true;
    }


    /**
     * 创建分期项目票ID
     *
     * @param InstallmentActivityID
     */
    public void ActivityCreateTicketID(String InstallmentActivityID) {
        ActivityDynamicModel activityDynamic = getActivityDynamicNoTran(InstallmentActivityID);

        if (activityDynamic == null) {
            return;
        }

        if (activityDynamic.getActivityState() != ActivityDetailModel.ONLINE_ACTIVITY_NOSTART) {
            return;
        }

        int TotalLines = activityDynamic.getActivityTotalLines();

        ticketService.CreateTickID(InstallmentActivityID, TotalLines);
    }


    /**
     * 设置项目完成
     *
     * @param ActivityID
     */
    public void SetActivityEnd(String ActivityID) {
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDao.getActivityVerifyCompleteModelNoTransaction(ActivityID);
        if (activityVerifyCompleteModel.IsEnoughInstallmentNum()) {
            SetActivityStatus(ActivityID, ActivityVerifyModel.STATUS_RAISE_FINISH);
        }
    }

    /**
     * 设置分期项目完成
     *
     * @param InstallmentActivityID
     */
    public void SetInstallmentActivityEnd(final String InstallmentActivityID) {

        final boolean[] activityDynamicModelIsEnough = {false};
        final boolean[] activityVerifyCompleteModelIsEnoughInstallmentNum = {false};

        final String[] ActivityID = {""};
        final int[] Installment = {0};

       if( activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);
                if (activityDynamicModel.IsEnough()) {

                    activityDynamicModelIsEnough[0] = true;

                    SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);

                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

                    int CurInstallmentNum = activityVerifyCompleteModel.getCurInstallmentNum();
                    CurInstallmentNum++;
                    activityVerifyCompleteModel.setCurInstallmentNum(CurInstallmentNum);
                    activityDao.updateNoTransaction(activityVerifyCompleteModel);

                    //发奖
/*                    if(lotteryService.StartLottery( InstallmentActivityID ) == null){
                        return false;
                    }*/


                    if (!activityVerifyCompleteModel.IsEnoughInstallmentNum()) {
                        //开启下一期
                        activityVerifyCompleteModelIsEnoughInstallmentNum[0] = true;
                        ActivityID[0] = activityVerifyCompleteModel.getActivityId();
                        Installment[0] = CurInstallmentNum+1;
                    }

                    //如果所有分期项目完成  设置父项目完成
                    SetActivityEnd(activityDynamicModel.getActivityVerifyCompleteModel().getActivityId());
                }
                return true;
            }
        })== Config.SERVICE_SUCCESS){

           if( activityDynamicModelIsEnough[0] == true ){
               MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERY_TOPIC,
                       MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERY_TAG, InstallmentActivityID, InstallmentActivityID));
           }


           if( activityVerifyCompleteModelIsEnoughInstallmentNum[0] == true ){
               Map<String, Object> map = new HashMap<String, Object>();
               map.put("ActivityID", ActivityID[0]);
               map.put("Installment", Installment[0]);

               String json = GsonUntil.JavaClassToJson( map );
               MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENT_TOPIC,
                       MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENT_TAG,json, InstallmentActivityID));
           }
       }

    }

    /**
     * 获取项目详情
     *
     * @param activityId
     * @return
     */
    public ActivityVerifyCompleteModel getActivityInformation(String activityId) {
        return (ActivityVerifyCompleteModel) activityDao.load(ActivityVerifyCompleteModel.class, activityId);
    }

    /**
     * 获取项目投资详情（领投金额、收益层次等)
     *
     * @param activityStageId
     * @return
     */
    public ActivityDetailModel getActivityInvestInfo(String activityStageId) {
        return activityDao.getActivityInvestInfo(activityStageId);
    }

    /**
     * 获得分期项目信息
     *
     * @param InstallmentActivityID
     * @return
     */
    public String GetInstaInstallmentActivityInfo(final String InstallmentActivityID) {
        final int[] TotalLines = {0};
        final int[] CurLines = {0};
        final int[] TotalLinePeoples = {0};
        final int[] CurLinePeoples = {0};
        final int[] TotalActivityLines = {0};
        final int[] CurActivityLine = {0};
        final int[] InstallmentTotalLinesPeoplse = {0};
        final int[] CurInstallmentNum = {0};
        final int[] TotalInstallmentNum = {0};
        activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);

                if (activityDynamicModel == null) {
                    return false;
                }

                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

                if (activityVerifyCompleteModel == null) {
                    return false;
                }

                TotalLines[0] = activityDynamicModel.getActivityTotalLines();
                CurLines[0] = activityDynamicModel.getActivityCurLines();
                TotalLinePeoples[0] = activityVerifyCompleteModel.getTotalLinePeoples();
                CurLinePeoples[0] = activityVerifyCompleteModel.getCurLinePeoples();
                TotalActivityLines[0] = activityVerifyCompleteModel.getTotalLines();
                CurActivityLine[0] = activityVerifyCompleteModel.getCurLines();
                InstallmentTotalLinesPeoplse[0] = activityDynamicModel.getActivityTotalLinesPeoples();
                TotalInstallmentNum[0] = activityVerifyCompleteModel.getTotalInstallmentNum();
                CurInstallmentNum[0] = activityVerifyCompleteModel.getCurInstallmentNum();
                return true;
            }
        });

        if( TotalLines[0] == 0 || InstallmentTotalLinesPeoplse[0] == 0 ){
            return null;
        }

        int InstallmentPurchNum = 0;
        int InstallmentAdvance = 0;
        int LinePeoples = 0;
        if( CurInstallmentNum[0] >= TotalInstallmentNum[0] ){
            InstallmentPurchNum = TotalLines[0] - CurLines[0];
            InstallmentAdvance = 1;
            LinePeoples = 1;
        }else{
             InstallmentPurchNum = TotalLines[0] - CurLines[0];
             InstallmentAdvance = (TotalActivityLines[0] - CurActivityLine[0])/TotalLines[0];
             LinePeoples = (TotalLinePeoples[0] - CurLinePeoples[0])/InstallmentTotalLinesPeoplse[0];
        }


        List list = new ArrayList();
        list.add(InstallmentPurchNum);
        list.add( InstallmentAdvance );
        list.add( LinePeoples );

        return GsonUntil.JavaClassToJson( list );
    }

    public void changeActivityStatus(String activityId, int status){
        activityDao.changeActivityStatus(activityId, status);
    }

    public void Test( String ActivityID,String josn ){

        ActivityVerifyCompleteModel activityVerifyCompleteModel = (ActivityVerifyCompleteModel)activityDao.load( ActivityVerifyCompleteModel.class,ActivityID );
        activityVerifyCompleteModel.setEarningPeoples( josn );
        activityDao.update( activityVerifyCompleteModel );
    }

}
