package com.money.Service.Lottery;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.Wallet.WalletService;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import com.money.dao.LotteryDAO.LotteryDAO;
import com.money.dao.PrizeListDAO.PrizeListDAO;
import com.money.dao.TicketDAO.TicketDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.dao.userDAO.UserDAO;
import com.money.job.LotteryPushJob;
import com.money.model.*;
import org.hibernate.Session;
import org.quartz.DateBuilder;
import org.quartz.SchedulerException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.QuartzUntil;
import until.ScheduleJob;

import java.text.ParseException;
import java.util.*;

/**
 * <p>User: 刘旻
 * <p>Date: 15-7-17
 * <p>Version: 1.0
 */

@Service("LotteryService")
public class LotteryService extends ServiceBase implements ServiceInterface {

    @Autowired
    LotteryDAO lotteryDAO;

    @Autowired
    TicketDAO ticketDAO;

    @Autowired
    ActivityService activityService;

    @Autowired
    activityDAO activityDAO;

    @Autowired
    WalletService walletService;


    @Autowired
    PrizeListDAO prizeListDAO;

    @Autowired
    UserDAO userDAO;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LotteryService.class);

    /**
     * 根据项目组的中奖列表发奖
     *
     * @param InstallmentActivityID
     * @return
     */
    public String StartLottery(final String InstallmentActivityID) throws Exception {
        final List<LotteryPeoples>[] listPeoples = new List[1];
        final String[] ActivityName = new String[1];
        if (Objects.equals(lotteryDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDetailModel activityDetailModel = activityService.getActivityDetailsNoTran(InstallmentActivityID);
                if (activityDetailModel == null) {
                    return false;
                }

                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                if (activityVerifyCompleteModel == null) {
                    return false;
                }

                ActivityName[0] = activityVerifyCompleteModel.getName();


                Set<SREarningModel> srEarningModelSet = activityDetailModel.getSrEarningModels();
                Set<SREarningModel> srEarningModelSet1 = activityDetailModel.getActivityVerifyCompleteModel().getSrEarningModels();
                srEarningModelSet.addAll(srEarningModelSet1);

                listPeoples[0] = StartLottery(InstallmentActivityID, srEarningModelSet);
                if (listPeoples[0] == null) {
                    LOGGER.error("中奖列表位空", InstallmentActivityID);
                    return false;
                }

                //同组完成后 给人打钱
                if (IsGroupCompelete(activityDetailModel)) {
                    SomeFarmByPrizeList(InstallmentActivityID);
                }

                //删除本期的购买表
                String DBName = Config.ACTIVITYGROUPTICKETNAME + InstallmentActivityID;
                lotteryDAO.DropList(DBName);

                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            //发送消息
            SendLotteryMessage(listPeoples[0], ActivityName[0]);
        }

        return Config.SERVICE_SUCCESS;
    }


    /**
     * @param InstallmentActivityID 分期项目ID
     * @param srEarningModelSet     总共中奖的人数
     * @param srEarningModelSet     得奖的层次和每个层次的人数
     * @return
     */
    public List<LotteryPeoples> StartLottery(String InstallmentActivityID, Set<SREarningModel> srEarningModelSet) {
        List<LotteryPeoples> listPeoples = lotteryDAO.GetRandNotLottery(InstallmentActivityID, srEarningModelSet);

        if (listPeoples == null) {
            LOGGER.error( "中奖人物列表位空",InstallmentActivityID,srEarningModelSet );
            return null;
        }

        int Index = 0;
        Iterator<SREarningModel> it = srEarningModelSet.iterator();
        int EarningNum = 0;
        while (it.hasNext()) {
            SREarningModel str = it.next();
            int LotteryLines = str.getEarningPrice();
            int PeoplesLines = str.getNum();
            EarningNum += LotteryLines * PeoplesLines;

            if (str.getEarningType() == Config.PURCHASELOCALTYRANTS) {
                for (LotteryPeoples TempListPeople : listPeoples) {
                    if (TempListPeople.getPurchaseType() == Config.PURCHASELOCALTYRANTS) {
                        TempListPeople.setLotteryLines(LotteryLines);
                        TempListPeople.setActivityID(InstallmentActivityID);
                    }
                }
            } else {
                for (int i = 0; i < PeoplesLines; ++i) {
                    if (Index >= listPeoples.size()) {
                        LOGGER.error( "发奖Index >= listPeoples.size()",Index,listPeoples.size() );
                        return null;
                    }

                    if (listPeoples.get(Index).getPurchaseType() == Config.PURCHASELOCALTYRANTS) {
                        continue;
                    }

                    listPeoples.get(Index).setLotteryLines(LotteryLines);
                    listPeoples.get(Index).setActivityID(InstallmentActivityID);
                    Index++;
                }
            }
        }

        //刷新个人的收益记录
        if (!updateEarnings(listPeoples)) {
            LOGGER.error( "刷新个人中奖记录错误",listPeoples );
            return null;
        }

        String json = GsonUntil.JavaClassToJson(listPeoples);
        PrizeListModel prizeListModel = new PrizeListModel();
        prizeListModel.setActivityIID(InstallmentActivityID);
        prizeListModel.setIsPrize(false);
        prizeListModel.setPrizeSituation(json);
        try {
            prizeListModel.setPrizeDate(MoneyServerDate.getDateCurDate());
        } catch (ParseException ignored) {

        }
        lotteryDAO.saveOrupdateNoTransaction(prizeListModel);

        //刷新领奖的记录
        EarningsRecordModel earningsRecordModel = new EarningsRecordModel();
        earningsRecordModel.setActivityStageId(InstallmentActivityID);
        try {
            earningsRecordModel.setEndDate(MoneyServerDate.getDateCurDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ActivityDetailModel activityDetailModel = activityDAO.getActivityDetaillNoTransaction(InstallmentActivityID);

        if (activityDetailModel == null) {
            return null;
        }

        earningsRecordModel.setTotalPrize(EarningNum);
        earningsRecordModel.setTotalFund(activityDetailModel.getTargetFund());
        earningsRecordModel.setActivityID(activityDetailModel.getActivityVerifyCompleteModel().getActivityId());
        lotteryDAO.saveNoTransaction(earningsRecordModel);
        return listPeoples;
    }

    /**
     * 判断同组是否完成
     *
     * @param activityDetailModel
     * @return
     */
    private boolean IsGroupCompelete(ActivityDetailModel activityDetailModel) {
        List<ActivityDetailModel> list = activityDAO.getActivityDetailByGroupID(activityDetailModel.getActivityVerifyCompleteModel().getActivityId(),
                activityDetailModel.getGroupId());

        for (ActivityDetailModel temp : list) {
            if (temp.getStatus() != ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE) {
                return false;
            }
        }

        return true;
    }


    /**
     * 给人钱包充值
     *
     * @param InstallmentActivityID
     */
    void SomeFarmByPrizeList(String InstallmentActivityID) throws Exception {
        ActivityDetailModel activityDetailModel = activityDAO.getActivityDetaillNoTransaction(InstallmentActivityID);

        String ActivityID = activityDetailModel.getActivityVerifyCompleteModel().getActivityId();
        int GroupID = activityDetailModel.getGroupId();

        List<ActivityDetailModel> list = activityDAO.getActivityDetailByGroupID(ActivityID, GroupID);

        for (ActivityDetailModel it : list) {
            String ActivityStageId = it.getActivityStageId();

            PrizeListModel prizeListModel = (PrizeListModel) prizeListDAO.loadNoTransaction(PrizeListModel.class, ActivityStageId);

            if (prizeListModel == null) {
                continue;
            }

            if (prizeListModel.isPrize()) {
                continue;
            }

            String json = prizeListModel.getPrizeSituation();
            List<LotteryPeoples> LotteryPeoplesList = GsonUntil.jsonToJavaClass(json, new TypeToken<List<LotteryPeoples>>() {
            }.getType());

            if (LotteryPeoplesList == null) {
                continue;
            }

            for (LotteryPeoples Peoples : LotteryPeoplesList) {
                walletService.RechargeWallet(Peoples.getUserId(), Peoples.getLotteryLines());
            }
            prizeListModel.setIsPrize(true);
            prizeListDAO.updateNoTransaction(prizeListModel);
        }
    }


    private boolean updateEarnings(List<LotteryPeoples> listPeoples) {
        if (listPeoples == null) {
            return false;
        }

        for (LotteryPeoples itLotteryPeoples : listPeoples) {
            String UserID = itLotteryPeoples.getUserId();

            if (userDAO.getUSerModelNoTransaction(UserID) == null) {
                return false;
            }

            UserEarningsModel userEarningsModel = new UserEarningsModel();
            userEarningsModel.setUserID(UserID);
            userEarningsModel.setUserEarningLines(itLotteryPeoples.getLotteryLines());
            userEarningsModel.setUserEarningsType(UserEarningsModel.ACTIVITYTYPE);
            userEarningsModel.setPurchaseType( itLotteryPeoples.getPurchaseType() );
            try {
                userEarningsModel.setUserEarningsDate(MoneyServerDate.getDateCurDate());
            } catch (ParseException ignored) {
            }
            userEarningsModel.setActivityStageId(itLotteryPeoples.getActivityID());

            userDAO.saveNoTransaction(userEarningsModel);
        }

        return true;
    }

    /**
     * 加入定时器  防止一次过多发送消息
     * @param lotteryPeoples
     * @param ActivityName
     */
    private void SendLotteryMessage(List<LotteryPeoples> lotteryPeoples, String ActivityName) {
        List<LotteryPeoples> Temp = new ArrayList();
        Map<String, Object> maptemp = new HashMap();
        int TempIndex = 0;
        int GroupNum = 0;
        for (LotteryPeoples it : lotteryPeoples) {
            if (GroupNum >= 20) {
                TempIndex++;

                maptemp.put(ActivityName, Temp);
                String json = GsonUntil.JavaClassToJson(maptemp);

                ScheduleJob job = new ScheduleJob();
                job.setJobGroup( " SendLotteryMessage" );
                int time = (TempIndex+1)*10;
                job.setCronExpression( time+" * * * * ?" );
                job.setJobId( it.getActivityID()+TempIndex );
                job.setJobName( it.getActivityID()+TempIndex );
                job.setJobStatus( "1" );
                job.setDesc( json );
                try {
                    QuartzUntil.getQuartzUntil().AddTick( job,LotteryPushJob.class, DateBuilder.nextGivenSecondDate(null,time));
                } catch (SchedulerException e) {
                    return;
                }

                Temp.clear();
                maptemp.clear();
                GroupNum = 0;
            }
            Temp.add(it);
            GroupNum++;
        }

        maptemp.put(ActivityName, Temp);
        String json = GsonUntil.JavaClassToJson(maptemp);
        maptemp.put(ActivityName, json);

        if( Temp.size() != 0 ){
            ScheduleJob job = new ScheduleJob();
            job.setJobGroup( " SendLotteryMessage" );
            int time = (TempIndex+1)*10;
            job.setCronExpression( time+" * * * * ?" );
            job.setJobId( Temp.get(0).getActivityID()+Integer.toString(TempIndex+1) );
            job.setJobName( Temp.get( 0 ).getActivityID()+Integer.toString(TempIndex+1) );
            job.setJobStatus( "1" );
            job.setDesc( json );
            try {
                QuartzUntil.getQuartzUntil().AddTick( job,LotteryPushJob.class, DateBuilder.nextGivenSecondDate(null,time));
            } catch (SchedulerException e) {
                return;
            }
        }

    }
}
