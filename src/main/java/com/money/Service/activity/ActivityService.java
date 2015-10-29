package com.money.Service.activity;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.Lottery.LotteryService;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceFactory;
import com.money.Service.ServiceInterface;
import com.money.Service.Ticket.TicketService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.*;

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

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

    public ActivityDetailModel getActivityDetails(String InstallmentActivityID) {
        return activityDao.getActivityDetails(InstallmentActivityID);
    }

    public ActivityDynamicModel getActivityDynamic(String InstallmentActivityID) {
        return activityDao.getActivityDynamic(InstallmentActivityID);
    }

    public ActivityDetailModel getActivityDetailsNoTran(String InstallmentActivityID) {
        return activityDao.getActivityDetaillNoTransaction(InstallmentActivityID);
    }

    public ActivityDynamicModel getActivityDynamicNoTran(String InstallmentActivityID) {
        return activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);
    }


    @SuppressWarnings("unchecked")
    public List<ActivityDetailModel> getAllActivityDetail(int pageIndex, int numPerPage) {
        return activityDao.getActivityListActivity(pageIndex, numPerPage);
    }

    public List<ActivityDetailModel> getAllActivityDetailTest(int pageIndex, int numPerPage) {
        return activityDao.getActivityListActivityTest(pageIndex, numPerPage);
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
        if (Objects.equals(activityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {

                ActivityDetailModel activityDetailModel = activityDao.getActivityDetails(activityID);
                activityDetailModel.setStatus(ActivityDetailModel.ONLINE_ACTIVITY_FAILED);
                activityDao.update(activityDetailModel);

                ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
                activityDao.delete(activitydynamicmodel);
            }
        }), Config.SERVICE_SUCCESS)) {
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

        if (Installment - 1 > 0) {
            String preInstallmentActivityID = ActivityID + "_" + Integer.toString(Installment - 1);

            ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(preInstallmentActivityID);

            if (activityDynamicModel == null) {
                return false;
            }

            if (activityDynamicModel.getActivityState() != ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE) {
                LOGGER.error("本期项目的上一期状态错误", ActivityID, activityDynamicModel.getActivityState());
                return false;
            }
        }

        String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);

        //创建分期项目票表
        activityDao.CreateTicketDB(InstallmentActivityID,
                activityDynamicModel.getActivityTotalLinesPeoples(),
                activityDynamicModel.getActivityTotalLines());
        //创建分期项目票ID 改为调用存储过程 与创建票表函数合并
        ActivityCreateTicketID(InstallmentActivityID);
        //预购项目
        if (purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance(ActivityID, InstallmentActivityID) == -1) {
            //购买错误
            LOGGER.error("项目分期预购错误", ActivityID, InstallmentActivityID);
            return false;
        }

        //设置项目开始
        SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_START);

        return true;
    }

    private boolean InstallmentActivityIDStartTest(String ActivityID, int Installment) throws Exception {

        if (Installment - 1 > 0) {
            String preInstallmentActivityID = ActivityID + "_" + Integer.toString(Installment - 1);

            ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(preInstallmentActivityID);

            if (activityDynamicModel == null) {
                return false;
            }

            if (activityDynamicModel.getActivityState() != ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE) {
                return false;
            }
        }

        String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);

        //创建分期项目票表
        activityDao.CreateTicketDB(InstallmentActivityID,
                activityDynamicModel.getActivityTotalLinesPeoples(),
                activityDynamicModel.getActivityTotalLines());
        //创建分期项目票ID 改为调用存储过程 与创建票表函数合并
        ActivityCreateTicketID(InstallmentActivityID);
        //预购项目
        if (purchaseInAdvance.PurchaseActivityFromPurchaseInAdvance(ActivityID, InstallmentActivityID) == -1) {
            //购买错误
            return false;
        }

        //设置项目开始
        SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_TEST);

        return true;
    }


    public void InstallmentActivityStart(final String ActivityID, final int Installment) throws Exception {
        if (Objects.equals(activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                return InstallmentActivityIDStart(ActivityID, Installment);
            }
        }), Config.SERVICE_SUCCESS)) {
            //预购项目已经完成  开始下一期
            String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
            SetInstallmentActivityEnd(InstallmentActivityID);
        }
    }


    public void InstallmentActivityStartTest(final String ActivityID, final int Installment) throws Exception {
        if (Objects.equals(activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                return InstallmentActivityIDStartTest(ActivityID, Installment);
            }
        }), Config.SERVICE_SUCCESS)) {
            //预购项目已经完成  开始下一期
            String InstallmentActivityID = ActivityID + "_" + Integer.toString(Installment);
            SetInstallmentActivityEnd(InstallmentActivityID);
        }
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
            }
        });
        return true;
    }

    public boolean ActivityCompleteStartTest(final String ActivityID) {
        activityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                //创建预购项目表
                activityDao.CreatePurchaseInAdvanceDB(ActivityID);
                //设置项目开始
                SetActivityStatus(ActivityID, ActivityVerifyModel.STATUS_START_RAISE);
                //设置第一期开始
                InstallmentActivityIDStartTest(ActivityID, 1);
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
        final boolean[] activityVerifyCompleteModelIsEnoughInstallmentNumTest = {false};


        final String[] ActivityID = {""};
        final int[] Installment = {0};

        if (Objects.equals(activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamicModelNoTransaction(InstallmentActivityID);
                if (activityDynamicModel.IsEnough() && activityDynamicModel.getActivityState() == ActivityDetailModel.ONLINE_ACTIVITY_START) {

                    activityDynamicModelIsEnough[0] = true;

                    SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);

                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

                    int CurInstallmentNum = activityVerifyCompleteModel.getCurInstallmentNum();
                    CurInstallmentNum++;


                    //完成的期和项目ID 对不上
                    String temp = activityVerifyCompleteModel.getActivityId() + "_" + Integer.toString(CurInstallmentNum);
                    if (!InstallmentActivityID.equals(temp)) {
                        return false;
                    }

                    //发奖
/*                    if(lotteryService.StartLottery( InstallmentActivityID ) == null){
                        return false;
                    }*/


                    if (!activityVerifyCompleteModel.IsEnoughInstallmentNum() &&
                            activityVerifyCompleteModel.getStatus() == ActivityVerifyModel.STATUS_START_RAISE) {
                        //开启下一期
                        activityVerifyCompleteModelIsEnoughInstallmentNum[0] = true;
                        ActivityID[0] = activityVerifyCompleteModel.getActivityId();
                        Installment[0] = CurInstallmentNum + 1;
                    }

                    activityVerifyCompleteModel.setCurInstallmentNum(CurInstallmentNum);
                    activityDao.updateNoTransaction(activityVerifyCompleteModel);

                    //如果所有分期项目完成  设置父项目完成
                    SetActivityEnd(activityDynamicModel.getActivityVerifyCompleteModel().getActivityId());
                    return true;
                } else if (activityDynamicModel.IsEnough() && activityDynamicModel.getActivityState() == ActivityDetailModel.ONLINE_ACTIVITY_TEST) {
                    activityDynamicModelIsEnough[0] = true;

                    SetInstallmentActivityStatus(InstallmentActivityID, ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);

                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

                    int CurInstallmentNum = activityVerifyCompleteModel.getCurInstallmentNum();
                    CurInstallmentNum++;


                    //完成的期和项目ID 对不上
                    String temp = activityVerifyCompleteModel.getActivityId() + "_" + Integer.toString(CurInstallmentNum);
                    if (!InstallmentActivityID.equals(temp)) {
                        return false;
                    }

                    //发奖
/*                    if(lotteryService.StartLottery( InstallmentActivityID ) == null){
                        return false;
                    }*/


                    if (!activityVerifyCompleteModel.IsEnoughInstallmentNum() &&
                            activityVerifyCompleteModel.getStatus() == ActivityVerifyModel.STATUS_START_RAISE) {
                        //开启下一期
                        activityVerifyCompleteModelIsEnoughInstallmentNumTest[0] = true;
                        ActivityID[0] = activityVerifyCompleteModel.getActivityId();
                        Installment[0] = CurInstallmentNum + 1;
                    }

                    activityVerifyCompleteModel.setCurInstallmentNum(CurInstallmentNum);
                    activityDao.updateNoTransaction(activityVerifyCompleteModel);

                    //如果所有分期项目完成  设置父项目完成
                    SetActivityEnd(activityDynamicModel.getActivityVerifyCompleteModel().getActivityId());
                    return true;
                } else {
                    return false;
                }
            }
        }), Config.SERVICE_SUCCESS)) {

            if (activityDynamicModelIsEnough[0]) {
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERY_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERY_TAG, InstallmentActivityID, InstallmentActivityID));
            }


            if (activityVerifyCompleteModelIsEnoughInstallmentNum[0]) {
                Map<String, Object> map = new HashMap();
                map.put("ActivityID", ActivityID[0]);
                map.put("Installment", Installment[0]);

                String json = GsonUntil.JavaClassToJson(map);
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENT_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENT_TAG, json, InstallmentActivityID));
            }

            if (activityVerifyCompleteModelIsEnoughInstallmentNumTest[0]) {
                Map<String, Object> map = new HashMap();
                map.put("ActivityID", ActivityID[0]);
                map.put("Installment", Installment[0]);

                String json = GsonUntil.JavaClassToJson(map);
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENTTEST_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_INSTALLMENTTEST_TAG, json, InstallmentActivityID));
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

        if (TotalLines[0] == 0 || InstallmentTotalLinesPeoplse[0] == 0) {
            return null;
        }

        int InstallmentPurchNum;
        int InstallmentAdvance;
        int LinePeoples;
        if (CurInstallmentNum[0] >= TotalInstallmentNum[0]) {
            InstallmentPurchNum = TotalLines[0] - CurLines[0];
            InstallmentAdvance = 1;
            LinePeoples = 1;
        } else {
            InstallmentPurchNum = TotalLines[0] - CurLines[0];
            InstallmentAdvance = (TotalActivityLines[0] - CurActivityLine[0]) / TotalLines[0];
            LinePeoples = (TotalLinePeoples[0] - CurLinePeoples[0]) / InstallmentTotalLinesPeoplse[0];
        }


        List list = new ArrayList();
        list.add(InstallmentPurchNum);
        list.add(InstallmentAdvance);
        list.add(LinePeoples);

        return GsonUntil.JavaClassToJson(list);
    }

    public void changeActivityStatus(String activityId, int status) {
        activityDao.changeActivityStatus(activityId, status);
    }

    public void Test(String ActivityID, String josn) {

        ActivityVerifyCompleteModel activityVerifyCompleteModel = (ActivityVerifyCompleteModel) activityDao.load(ActivityVerifyCompleteModel.class, ActivityID);
        activityVerifyCompleteModel.setEarningPeoples(josn);
        activityDao.update(activityVerifyCompleteModel);
    }

    public List<Object> getActivityEarnings(final String UserID, final int Page, final int FindNum) {
        final List<Object> ListJson = new ArrayList();

        activityDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                List<UserEarningsModel> userEarningsModelList = session.createCriteria(UserEarningsModel.class)
                        .setMaxResults(FindNum)
                        .setFirstResult(Page * FindNum)
                        .addOrder(Order.desc("UserEarningsDate"))
                        .add(Restrictions.eq("UserID", UserID))
                        .list();

                for (UserEarningsModel userEarningsModel : userEarningsModelList) {

                    switch (userEarningsModel.getUserEarningsType()) {
                        case UserEarningsModel.ACTIVITYTYPE: {
                            ActivityDetailModel activityDetailModel = getActivityDetailsNoTran(userEarningsModel.getActivityStageId());
                            if (activityDetailModel == null) {
                                return false;
                            }

                            List<String> ActivityChildInfo = new ArrayList();
                            ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                            ActivityChildInfo.add(activityDetailModel.getActivityStageId());
                            ActivityChildInfo.add(activityVerifyCompleteModel.getName());
                            ActivityChildInfo.add(Integer.toString(activityVerifyCompleteModel.getTotalInstallmentNum()));
                            ActivityChildInfo.add(Integer.toString(activityDetailModel.getStageIndex()));
                            ActivityChildInfo.add(activityVerifyCompleteModel.getActivityId());
                            ActivityChildInfo.add(activityVerifyCompleteModel.getImageUrl());
                            ActivityChildInfo.add(Integer.toString(userEarningsModel.getUserEarningLines()));
                            ActivityChildInfo.add(userEarningsModel.getUserEarningsDate().toString());
                            ActivityChildInfo.add(Integer.toString(userEarningsModel.getId()));
                            ListJson.add(ActivityChildInfo);

                        }
                        break;
                        case UserEarningsModel.ACTIVITYPREFERENTIALTYPE: {
                            ActivityVerifyCompleteModel activityVerifyCompleteModel =
                                    activityDao.getActivityVerifyCompleteModelNoTransaction( userEarningsModel.getActivityStageId());

                            if( activityVerifyCompleteModel == null ){
                                return false;
                            }
                            List<String> ActivityChildInfo = new ArrayList();
                            ActivityChildInfo.add("0");
                            ActivityChildInfo.add(activityVerifyCompleteModel.getName());
                            ActivityChildInfo.add(Integer.toString(activityVerifyCompleteModel.getTotalInstallmentNum()));
                            ActivityChildInfo.add("0");
                            ActivityChildInfo.add(activityVerifyCompleteModel.getActivityId());
                            ActivityChildInfo.add(activityVerifyCompleteModel.getImageUrl());
                            ActivityChildInfo.add(Integer.toString(userEarningsModel.getUserEarningLines()));
                            ActivityChildInfo.add(userEarningsModel.getUserEarningsDate().toString());
                            ActivityChildInfo.add(Integer.toString(userEarningsModel.getId()));
                            ListJson.add(ActivityChildInfo);

                        }
                        break;
                    }
                }
                return true;
            }
        });

        return ListJson;
    }

}
