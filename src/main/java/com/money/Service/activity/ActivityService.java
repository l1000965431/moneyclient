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
    private UserService userService;

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

    @SuppressWarnings("unchecked")
    public List<ActivityDetailModel> getAllActivityDetail() {
        return activityDao.getActivityListActivity(0, 1000);
    }

    public List<ActivityDetailModel> getOrderDetailsList(int minpage, int maxpage) {
        try {
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 项目是否存在
     *
     * @param InstallmentActivityID 项目ID
     * @return
     */
    public boolean IsActivityExist(String InstallmentActivityID) {

        ActivityDetailModel activityModel = activityDao.getActivityDetails(InstallmentActivityID);

        if (activityModel == null) {
            return false;
        }

        return true;
    }

    /**
     * 项目ID是否合法
     *
     * @param activityID
     * @return
     */
    public boolean IsActivityIDLegal(int activityID) {

        return true;
    }

    /**
     * 设置项目的当前金额
     *
     * @param InstallmentActivityID 项目ID
     * @param addlines              投资金额
     * @return
     */
    public String SetActivityCurLines(String InstallmentActivityID, int addlines) {
        try {
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(InstallmentActivityID);
            SetActivityCurLines(activitydynamicmodel, addlines);
            activityDao.update(activitydynamicmodel);
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
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
     * @param InstallmentActivityID 项目ID
     * @return
     */
    private boolean IsActivityComplete(String InstallmentActivityID) {
        ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(InstallmentActivityID);
        return IsActivityComplete(activitydynamicmodel);
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
     * 获得金额对应的当前人数
     *
     * @param
     * @param Lines
     * @return
     */
    public int GetActivityLinesCurPeoples(ActivityDynamicModel activitydynamicmodel, int Lines) {
        try {
            int activitycurlines = activitydynamicmodel.getActivityCurLinesPeoples();
            Map<String, Integer> mappeoples = GsonUntil.jsonToJavaClass("", new TypeToken<Map<String, Integer>>() {
            }.getType());
            int peoples = mappeoples.get(Integer.toString(Lines));
            return peoples;
        } catch (Exception e) {
            return Config.RETURNERROR;
        }
    }

    /**
     * 获得对应金额的总人数
     *
     * @param
     * @param Lines 金额
     * @return
     */
    public int GetActivityLinesTotalPeoples(ActivityDetailModel activityDetailModel, int Lines) {
        try {
            String activitycurlines = activityDetailModel.getActivityLinesPeoples();
            Map<String, Integer> mappeoples = GsonUntil.jsonToJavaClass(activitycurlines, new TypeToken<Map<String, Integer>>() {
            }.getType());
            int peoples = mappeoples.get(Integer.toString(Lines));
            return peoples;
        } catch (Exception e) {
            return Config.RETURNERROR;
        }
    }


    /**
     * 检测当前金额的人数是否已满
     *
     * @param Lines 金额
     * @return
     */
    public boolean IsActivityLinesPeoplesFull(ActivityDetailModel activityDetailModel, ActivityDynamicModel activitydynamicmodel, int Lines) {
        int curLinesPeoples = GetActivityLinesCurPeoples(activitydynamicmodel, Lines);
        int totalLinesPeoples = GetActivityLinesTotalPeoples(activityDetailModel, Lines);

        if (curLinesPeoples != Config.RETURNERROR && totalLinesPeoples != Config.RETURNERROR && curLinesPeoples >= totalLinesPeoples) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 增加当前金额
     *
     * @param InstallmentActivityID 项目ID
     * @param addLines              增加的金额
     * @return
     */
    public String AddActivityLines(String InstallmentActivityID, int addLines) {
        try {
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(InstallmentActivityID);
            AddActivityLines(activitydynamicmodel, addLines);
            activityDao.update(activitydynamicmodel);
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 增加当前金额
     *
     * @param addLines 增加的金额
     * @return
     */
    public String AddActivityLines(ActivityDynamicModel activitydynamicmodel, int addLines) {
        try {
            int curLines = activitydynamicmodel.getActivityCurLines();
            curLines += addLines;
            activitydynamicmodel.setActivityCurLines(curLines);
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
    }


    /**
     * 设置活动完成
     *
     * @return
     */
    public String SetActivityCompelete(ActivityDetailModel activityDetailModel, ActivityDynamicModel activitydynamicmodel) {
        try {
            int curactivitylines = activitydynamicmodel.getActivityCurLines();
            int totalactivitylines = activitydynamicmodel.getActivityTotalAmount();

            if (curactivitylines >= totalactivitylines) {
                activityDetailModel.setStatus(ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);
            }
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
    }


    /**
     * 分页查找项目详情
     *
     * @param pagenumber 每页显示多少条信息
     * @param page       显示第几页
     * @return
     */
    public List GetActivityDetailsWithPage(int pagenumber, int page) {

        return null;
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
     * 后台修改项目的状态
     */
    public String SetActivityState(int State, String ActivityID) {

        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel) activityDao.load(ActivityVerifyModel.class, ActivityID);

        activityVerifyModel.setAuditorStatus(State);

        activityDao.update(activityVerifyModel);

        return null;
    }

    /**
     * 获得项目剩余的期数
     *
     * @param ActivityID 父项目ID
     * @return
     */
    public int getActivityRemainingInstallment(String ActivityID) {

        return 0;
    }

    /**
     * 获得分期项目剩余的票数
     *
     * @param InstallmentActivityID 分期项目ID
     * @return
     */

    public int getActivityRemainingTickets(String InstallmentActivityID) {

        return 0;
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
            public void callback(Session session) throws Exception {
                activityDetailModel.setStatus(Status);
                activityDynamicModel.setActivityState(Status);

                session.update(activityDetailModel);
                session.update(activityDynamicModel);
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
            public void callback(Session session) throws Exception {
                activityVerifyCompleteModel.setStatus(Status);
                session.update(activityVerifyCompleteModel);
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
