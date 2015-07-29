package com.money.Service.Lottery;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.LotteryDAO.LotteryDAO;
import com.money.dao.TicketDAO.TicketDAO;
import com.money.model.ActivityGroupModel;
import com.money.model.LotteryModel;
import com.money.model.LotteryPeoples;
import com.money.model.PrizeListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneySeverRandom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * @param ActivityGroupID
     * @return
     */
    public String StartLottery(int ActivityGroupID) {

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
     * @param ActivityID     分期项目ID
     * @param LotteryPeoples 总共中奖的人数
     * @param mapLottery     得奖的层次和每个层次的人数
     * @return
     */
    public String StartLottery(String ActivityID, int LotteryPeoples, Map<Integer, Integer> mapLottery) {
        List<LotteryPeoples> listPeoples = lotteryDAO.GetRandNotLottery(ActivityID, LotteryPeoples);

        if( listPeoples == null ){
            return null;
        }

        Iterator it = mapLottery.entrySet().iterator();

        int Index = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            int LotteryLines = Integer.valueOf(entry.getKey().toString());
            int PeoplesLines = Integer.valueOf(entry.getValue().toString());

            for (int i = 0; i < PeoplesLines; ++i) {
                if (Index >= listPeoples.size()) {
                    return null;
                }

                listPeoples.get(Index).setLotteryLines(LotteryLines);
                Index++;
            }
        }

        String json = GsonUntil.JavaClassToJson(listPeoples);
        PrizeListModel prizeListModel = new PrizeListModel();
        prizeListModel.setActivityIID( ActivityID );
        prizeListModel.setIsPrize( false );
        prizeListModel.setPrizeSituation( json );
        lotteryDAO.save( prizeListModel );

        return json;
    }
}
