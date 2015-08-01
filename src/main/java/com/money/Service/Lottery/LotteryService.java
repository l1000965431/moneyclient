package com.money.Service.Lottery;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.LotteryDAO.LotteryDAO;
import com.money.dao.TicketDAO.TicketDAO;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneySeverRandom;

import java.util.*;

/**
 * <p>User: 刘旻
 * <p>Date: 15-7-17
 * <p>Version: 1.0
 */

@Service("LotteryService")
public class LotteryService extends ServiceBase implements ServiceInterface {

    /**
     * 土豪发奖
     */
    public static final int LOTTERYLOCALTYRANTS = 1;

    /**
     * 屌丝发奖
     */
    public static final int LOTTERYPRICKSILK = 2;

    @Autowired
    LotteryDAO lotteryDAO;

    @Autowired
    TicketDAO ticketDAO;

    @Autowired
    ActivityService activityService;

    @Autowired
    activityDAO activityDAO;

    //基础的中奖几率
    Map<Integer, Map<Integer, Integer>> MapLinesLotteryProbability;

    //基础的中奖区间
    Map<Integer, Map<Integer, Integer>> MapLinesLotteryInterval;

    ActivityGroupModel activityGroupModel;

    /**
     * 记录当前中奖的人数
     */
    Map<Integer, Integer> mapCurLotteryPeoples;

    /**
     * 基础投资金额
     */
    int baseLines = 0;

    /**
     * 发奖类型
     */
    int lotteryType = LOTTERYPRICKSILK;


    /**
     * 创建中奖列表
     *
     * @param ActivityGroupID
     * @return
     */
    public boolean CreateLotteryDB(int ActivityGroupID) {

        return false;
    }

    /**
     * 根据项目组的中奖列表发奖
     *
     * @param InstallmentActivityID
     * @return
     */
    public String StartLottery(String InstallmentActivityID) {
        ActivityDetailModel activityDetailModel = activityService.getActivityDetails(InstallmentActivityID);
        if (activityDetailModel == null) {
            return null;
        }

        Set<SREarningModel> srEarningModelSet = activityDetailModel.getSrEarningModels();
        //计算总共多少人中奖
        int TotalPeople = 0;
        Iterator<SREarningModel> it = srEarningModelSet.iterator();
        while (it.hasNext()) {
            SREarningModel str = it.next();
            TotalPeople += str.getNum();

        }


        StartLottery(InstallmentActivityID, TotalPeople, srEarningModelSet);

        //同组完成后 给人打钱
        if( IsGroupCompelete( activityDetailModel ) ){
            MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TOPIC,
                            MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TAG,InstallmentActivityID,"1")
            );
        }

        return null;
    }

    /**
     * 跟据票的序列号查看是否中奖
     *
     * @param ActivityID
     * @param TicketNumber
     * @return
     */
    public boolean IsLotteryWithTicketNumber(int ActivityID, long TicketNumber) {

        if (ticketDAO.IsLotteryTicket(ActivityID, TicketNumber)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param InstallmentActivityID 分期项目ID
     * @param LotteryPeoples        总共中奖的人数
     * @param srEarningModelSet            得奖的层次和每个层次的人数
     * @return
     */
    public String StartLottery(String InstallmentActivityID, int LotteryPeoples, Set<SREarningModel> srEarningModelSet) {
        List<LotteryPeoples> listPeoples = lotteryDAO.GetRandNotLottery(InstallmentActivityID, LotteryPeoples);

        if (listPeoples == null) {
            return null;
        }

        int Index = 0;
        Iterator<SREarningModel> it = srEarningModelSet.iterator();
        while (it.hasNext()) {
            SREarningModel str = it.next();
            int LotteryLines = str.getEarningPrice();
            int PeoplesLines = str.getNum();

            if( str.getEarningType() == Config.PURCHASELOCALTYRANTS ){
                for( LotteryPeoples TempListPeople:listPeoples ){
                     if( TempListPeople.getLotteryType() == Config.PURCHASELOCALTYRANTS ){
                         TempListPeople.setLotteryLines( PeoplesLines );
                     }
                }
            }else{
                for (int i = 0; i < PeoplesLines; ++i) {
                    if (Index >= listPeoples.size()) {
                        return null;
                    }

                    if( listPeoples.get(Index).getLotteryType() == Config.PURCHASELOCALTYRANTS ){
                        continue;
                    }

                    listPeoples.get(Index).setLotteryLines(LotteryLines);
                    Index++;

                }
            }
        }


        String json = GsonUntil.JavaClassToJson(listPeoples);
        PrizeListModel prizeListModel = new PrizeListModel();
        prizeListModel.setActivityIID(InstallmentActivityID);
        prizeListModel.setIsPrize(false);
        prizeListModel.setPrizeSituation(json);
        lotteryDAO.save(prizeListModel);

        return json;
    }

    /**
     * 判断同组是否完成
     * @param activityDetailModel
     * @return
     */
    private boolean IsGroupCompelete( ActivityDetailModel activityDetailModel ){
        List<ActivityDetailModel> list = activityDAO.getActivityDetailByGroupID( activityDetailModel.getActivityVerifyCompleteModel().getActivityId(),
                activityDetailModel.getGroupId() );

        for( ActivityDetailModel temp:list ){
            if( temp.getStatus() != ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE ){
                return false;
            }
        }

        return true;
    }


}
