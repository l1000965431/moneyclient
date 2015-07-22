package com.money.Service.Ticket;

import com.money.Service.Lottery.LotteryService;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceFactory;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.TicketDAO.TicketDAO;
import com.money.model.LotteryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.MoneySeverRandom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 项目票服务
 * 开奖说明:
 * 1.随机从购买列表中取出1000个没有中奖的买家 根据投注的金额和对用的几率 计算是否中奖
 * 2.如果中奖,设置买家中奖的金额
 * 3.如果没有中奖进入下一次抽奖机会
 * 4.如果所有奖励都已经发完 则取出剩下的买家，发给金点。发奖结束
 * 5.如果奖励没有发完,跳到1处 接着执行
 * 各阶段票服务
 * <p>User: liumin
 * <p>Date: 15-7-19
 * <p>Version: 1.0
 */
@Service("TicketService")
public class TicketService extends ServiceBase implements ServiceInterface {

    /**
     * 土豪发奖
     */
    public static final int LOTTERYLOCALTYRANTS = 1;

    /**
     * 屌丝发奖
     */
    public static final int LOTTERYPRICKSILK = 2;

    @Autowired
    TicketDAO ticketDAO;

    //基础的中奖几率
    Map<Integer, Map<Integer, Integer>> MapLinesLotteryProbability;

    //基础的中奖区间
    Map<Integer, Map<Integer, Integer>> MapLinesLotteryInterval;

    /**
     * 记录当前中奖的人数
     */
    Map<Integer, Integer> mapCurLotteryPeoples;

    /**
     * 记录对应金额总共的中奖的人数
     */
    Map<Integer, Integer> mapTotalLotteryPeoples;

    /**
     * 投资层次
     */
    List<Integer> LinesGradation;

    /**
     * 中间层次
     */
    List<Integer> LotteryGradation;

    /**
     * 基础投资金额
     */
    int baseLines = 0;

    /**
     * 发奖类型
     */
    int lotteryType;


    /**
     * 根据项目组ID生成票的序列表
     *
     * @param ActivityGroupID
     * @param mapTicketDetails 票的分布详情 小R和大R全部在一起 小R对应项目组大R对应各个项目 Map<项目ID,Map<金额,数量>>
     * @return
     */
    public String CreateTicketWithNumber(int ActivityGroupID, Map<Integer, Map<Integer, Integer>> mapTicketDetails) {
        String ticketname = Config.ACTIVITYGROUPTICKETNAME + Integer.toString(ActivityGroupID);

        //创建项目组票序列号表
        if (!ticketDAO.CreateTicketDBWihActivityID(ActivityGroupID)) {
            return null;
        }

        LotteryService lotteryService = ServiceFactory.getService("LotteryService");

        //创建项目组中奖表
        if (!lotteryService.CreateLotteryDB(ActivityGroupID)) {
            return null;
        }

        Iterator<Map.Entry<Integer, Map<Integer, Integer>>> TicketDetailsit = mapTicketDetails.entrySet().iterator();

        while (TicketDetailsit.hasNext()) {
            Map.Entry<Integer, Map<Integer, Integer>> entry = TicketDetailsit.next();

            int activityID = entry.getKey();
            Map<Integer, Integer> mapTicket = entry.getValue();
            Iterator<Map.Entry<Integer, Integer>> mapTicketIt = mapTicket.entrySet().iterator();
            while (mapTicketIt.hasNext()) {
                Map.Entry<Integer, Integer> Ticketentry = mapTicketIt.next();
                int Lines = Ticketentry.getKey();
                int number = Ticketentry.getValue();
                ticketDAO.InsertTickDB(activityID, Lines, number);
            }
        }


        return null;
    }

    /**
     * 根基基础几率初始化几率区间
     *
     * @param ActivityGerupID         项目ID或项目组ID
     * @param LinesLotteryProbability 基础几率
     * @return 几率区间
     */
    public Map<Integer, Map<Integer, Integer>> InitLinesLotteryBaseProbability(int ActivityGerupID,
                                                                               Map<Integer, Map<Integer, Integer>> LinesLotteryProbability) {
        //生成的几率区间
        Map<Integer, Map<Integer, Integer>> LinesLotteryInterval = null;

        baseLines = LinesGradation.get(0);

        //数据库中存储的基础几率
        Map<Integer, Integer> mapBaseProbability = GetBasetLinesProbability(baseLines);

        //初始化各个投资层次的中奖几率
        Iterator it = LinesLotteryProbability.entrySet().iterator();
        //投资层次的索引
        int LinesGradationIndex = 0;
        int LotteryGradationIndex = 0;
        for (; LinesGradationIndex < LinesGradation.size(); ++LinesGradationIndex) {
            for (; LotteryGradationIndex < LotteryGradation.size(); ++LotteryGradationIndex) {
                int lotterylines = LotteryGradation.get(LinesGradationIndex);
                int linesGradation = LinesGradation.get(LinesGradationIndex);
                int lotteryProbability = mapBaseProbability.get(lotterylines);

                if (LinesLotteryProbability.get(linesGradation) == null) {
                    //生成中奖几率表
                    Map<Integer, Integer> lotteryProbabilityMap = new HashMap<Integer, Integer>();
                    int curlotteryProbability = lotteryProbability * (linesGradation / baseLines);

                    lotteryProbabilityMap.put(lotterylines, curlotteryProbability);
                    LinesLotteryProbability.put(linesGradation, lotteryProbabilityMap);

                    //生成中奖区间表

                    Map<Integer, Integer> lotteryintervalMap = new HashMap<Integer, Integer>();
                    int curlotteryinterval = curlotteryProbability * Config.PROBABILITYAMPLIFICATION;
                    lotteryintervalMap.put(lotterylines, curlotteryinterval);
                    LinesLotteryInterval.put(linesGradation, lotteryintervalMap);

                } else {
                    Map<Integer, Integer> lotteryProbabilityMap = LinesLotteryProbability.get(linesGradation);
                    int curlotteryProbability = lotteryProbability * (linesGradation / baseLines);
                    lotteryProbabilityMap.put(lotterylines, curlotteryProbability);
                    LinesLotteryProbability.put(linesGradation, lotteryProbabilityMap);

                    //当前区间的初始值
                    Map<Integer, Integer> lotteryintervalMap = LinesLotteryInterval.get(linesGradation);
                    int preinterval = LinesGradation.get(LinesGradationIndex - 1);
                    int curlotteryinterval = curlotteryProbability * Config.PROBABILITYAMPLIFICATION + lotteryintervalMap.get(LotteryGradationIndex - 1);
                    lotteryintervalMap.put(lotterylines, curlotteryinterval);
                    LinesLotteryInterval.put(linesGradation, lotteryintervalMap);
                }
            }
        }

        return LinesLotteryInterval;
    }

    /**
     * 根据投资金额获取基础中奖几率
     *
     * @param InvestmentLines
     * @return
     */
    Map<Integer, Integer> GetBasetLinesProbability(int InvestmentLines) {

        return null;

    }

    /**
     * 根据投资金额获取中奖区间
     *
     * @param InvestmentLines
     * @return
     */
    Map<Integer, Integer> GetBasetLinesInterval(int InvestmentLines) {
        try {
            Map<Integer, Integer> map = MapLinesLotteryInterval.get(InvestmentLines);
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得中奖金额的总人数
     *
     * @param LotteryLinse
     * @return
     */
    int GetLinesLotteryTotalPeoples(int LotteryLinse) {
        try {
            int curlinespeoples = mapTotalLotteryPeoples.get(LotteryLinse);
            return curlinespeoples;
        } catch (Exception e) {
            return Config.RETURNERROR;
        }
    }

    /**
     * 获得中奖金额当前的人数
     *
     * @param LotteryLinse
     * @return
     */
    int GetLinesLotteryCurPeoples(int LotteryLinse) {
        try {
            int curlinespeoples = mapCurLotteryPeoples.get(LotteryLinse);
            return curlinespeoples;
        } catch (Exception e) {
            return Config.RETURNERROR;
        }
    }

    /**
     * 对应金额的中奖人数是否已满
     *
     * @param Lines
     * @return
     */
    boolean IsLinesLotteryCompelete(int Lines) {
        int curlinespeoples = GetLinesLotteryTotalPeoples(Lines);
        int totalLinespeoples = GetLinesLotteryCurPeoples(Lines);

        if (curlinespeoples != Config.RETURNERROR
                && totalLinespeoples != Config.RETURNERROR
                && curlinespeoples >= totalLinespeoples) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 所有的奖励是否已经发完
     *
     * @return
     */
    boolean IsAllLinesLotteryConpelete() {
        for (int i = 0; i < LotteryGradation.size(); ++i) {

            if (!IsLinesLotteryCompelete(LotteryGradation.get(i))) {
                return false;
            }
        }

        return true;
    }


    void AddLotteryLinesPeoples(int LotteryLines) {
        Integer LotteryLinesPeoples = mapCurLotteryPeoples.get(LotteryLines);
        if (LotteryLinesPeoples == null) {
            return;
        }

        LotteryLinesPeoples++;
        mapCurLotteryPeoples.put(LotteryLines, LotteryLinesPeoples);
    }


    /**
     * 根据票的投资金额发奖
     *
     * @param lotteryModel
     */
    void StartLotteryByLines(LotteryModel lotteryModel) {

        int InvestmentLines = lotteryModel.getLines();
        //当前投资额的中奖区间
        Map<Integer, Integer> LotteryInterval = GetBasetLinesInterval(InvestmentLines);
        if (LotteryInterval == null) {
            return;
        }

        Iterator it = LotteryInterval.entrySet().iterator();

        int random = MoneySeverRandom.getRandomNum(0, Config.PROBABILITYAMPLIFICATION * 10);

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            int value = (Integer) entry.getValue();
            int lotteryLines = (Integer) entry.getKey();
            if (IsLinesLotteryCompelete(lotteryLines) && random >= value) {
                //中奖
                AddLotteryLinesPeoples(lotteryLines);
                lotteryModel.setLottery(lotteryLines);

                System.out.printf(Integer.toString(lotteryModel.getUserid()) + Integer.toString(lotteryModel.getLottery()));
            }
        }
    }

    /**
     * 开始发奖
     *
     * @param LotteryPeople 参与抽奖的人列表
     * @return
     */
    boolean StartLottery(List LotteryPeople) {
        if (LotteryPeople == null) {
            return false;
        }

        for (int i = 0; i < LotteryPeople.size(); ++i) {
            StartLotteryByLines((LotteryModel) LotteryPeople.get(i));
        }

        return true;
    }

    /**
     * 开始发奖
     *
     * @return
     */
    public boolean StartLottery(int ActivityGroupID) {

        int testindex = 0;
        long orderTime = System.currentTimeMillis();
        while (true) {
            testindex++;
            if (testindex >= 20) {
                orderTime = System.currentTimeMillis() - orderTime;
                System.out.printf(Long.toString(orderTime));
                return false;
            }


            //所有奖项发完 给没有中奖的人发放金点
            if (IsAllLinesLotteryConpelete()) {
                //发放金点
                /*List listNoLotteryPeoples = GetNoLotteryUser( ActivityGroupID );
                LotteryGoldPoint( listNoLotteryPeoples );*/
                return true;
            }

            List ListPeople = GetRandomUser(ActivityGroupID);
            if (!StartLottery(ListPeople)) {
                return false;
            }
        }
    }


    /**
     * 随机获取没有获奖的人列表
     *
     * @return
     */
    public List GetRandomUser(int ActivityGroupID) {
        if (lotteryType == LOTTERYPRICKSILK) {
            return ticketDAO.GetRandNotLottery(ActivityGroupID);
        } else {
            return null;
        }
    }


    /**
     * 获取没有获奖的人列表
     *
     * @return
     */
    public List GetNoLotteryUser(int ActivityGroupID) {
        if (lotteryType == LOTTERYPRICKSILK) {
            return null;
        } else {
            return null;
        }
    }

    /**
     * 金点发放
     *
     * @param GlodPoinPeoples
     */
    void LotteryGoldPoint(List GlodPoinPeoples) {
        if (GlodPoinPeoples == null) {
            return;
        }
    }

}
