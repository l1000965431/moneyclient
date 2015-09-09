package com.money.Service.GroupActivity;

import com.google.gson.reflect.TypeToken;
import com.money.Service.GlobalConifg.GlobalConfigService;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceFactory;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.MoneySeverRandom;

import java.util.*;

/**
 * Created by happysky on 15-7-15.
 */
@Service("ServiceGroupActivity")
public class ServiceGroupActivity extends ServiceBase implements ServiceInterface {
    @Autowired
    private activityDAO generaDAO;

    /**
     * 设置项目大小R比例
     */
    public void setActivityInvestProportion(String activityId, float srProportion, float brProportion) {
        ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel) generaDAO.load(ActivityVerifyCompleteModel.class, activityId);
        if (completeModel == null) {
            return;
        }

        completeModel.setSrInvestProportion(srProportion);
        completeModel.setBrInvestProportion(brProportion);
        generaDAO.update(completeModel);
    }

    /**
     * 项目分期操作
     * @param ActicityID
     * @param AdvanceNum 多少组
     * @param PurchaseNum 一组多少个
     */
    public void splitActivityByStage(int ntargetFund ,String ActicityID, final int AdvanceNum, final int PurchaseNum) {

        final ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel) generaDAO.load(ActivityVerifyCompleteModel.class, ActicityID);

        int targetFund = ntargetFund;
        int srInvestProportion = completeModel.getTotalLines();
        int brInvestProportion = completeModel.getTotalLinePeoples();

        final List list = CalculateActivityEarnings(targetFund, AdvanceNum * PurchaseNum, srInvestProportion, brInvestProportion);

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                for (int i = 0; i < AdvanceNum * PurchaseNum; i++) {
                    ActivityDetailModel activityDetailModel = new ActivityDetailModel();
                    ActivityDynamicModel activityDynamicModel = new ActivityDynamicModel();

                    String Index = Integer.toString(i + 1);
                    String ActivityID = completeModel.getActivityId();
                    activityDetailModel.setActivityStageId(ActivityID + "_" + Index);

                    //最后一期
                    if (i == AdvanceNum * PurchaseNum - 1) {
                        //每期总金额
                        activityDetailModel.setTargetFund((Integer) list.get(1) + (Integer) list.get(3));
                        activityDynamicModel.setActivityTotalAmount((Integer) list.get(1) + (Integer) list.get(3));

                        //每期大R小R金额
                        activityDynamicModel.setActivityTotalLines((Integer) list.get(1));
                        activityDynamicModel.setActivityTotalLinesPeoples((Integer) list.get(3));
                    } else {
                        //平常期
                        //每期总金额
                        activityDetailModel.setTargetFund((Integer) list.get(0) + (Integer) list.get(2));
                        activityDynamicModel.setActivityTotalAmount((Integer) list.get(0) + (Integer) list.get(2));

                        //每期大R小R金额
                        activityDynamicModel.setActivityTotalLines((Integer) list.get(0));
                        activityDynamicModel.setActivityTotalLinesPeoples((Integer) list.get(2));
                    }

                    activityDetailModel.setActivityVerifyCompleteModel(completeModel);
                    activityDetailModel.setGroupId((i / PurchaseNum) + 1);
                    activityDetailModel.setActivityStartTime(MoneyServerDate.getDateCurDate());
                    activityDetailModel.setStageIndex(i + 1);

                    activityDynamicModel.setActivityStageId(activityDetailModel.getActivityStageId());
                    activityDynamicModel.setActivityVerifyCompleteModel(completeModel);
                    activityDynamicModel.setGroupId((i / PurchaseNum) + 1);

                    activityDynamicModel.setActivityDetailModel(activityDetailModel);
                    activityDetailModel.setDynamicModel(activityDynamicModel);
                    generaDAO.saveNoTransaction(activityDetailModel);
                }
                return true;
            }
        });

        completeModel.setTotalInstallmentNum(AdvanceNum * PurchaseNum);
        generaDAO.update(completeModel);
    }

    public HashSet<SREarningModel> calcEarningPrize(int earningAmount, List<Integer> earningLevelList, List<Float> earningProportionList, int ticketsNum) {
        HashMap<Integer, Integer> tickets = new HashMap<Integer, Integer>();
        int earningPriceLeft = earningAmount;
        for (int i = 0; i < earningLevelList.size(); i++) {
            int price = (int) (earningAmount * earningProportionList.get(i));
            price = price - price % earningLevelList.get(i);
            earningPriceLeft = earningPriceLeft - price;
            tickets.put(earningLevelList.get(i), price / earningLevelList.get(i));
        }
        int leftMinTicket = earningPriceLeft / earningLevelList.get(0);
        int minTicketNum = tickets.get(earningLevelList.get(0));
        tickets.put(earningLevelList.get(0), leftMinTicket + minTicketNum);

        HashSet<SREarningModel> ticketModels = new HashSet<SREarningModel>();
        Iterator iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            SREarningModel model = new SREarningModel();
            Map.Entry entry = (Map.Entry) iterator.next();
            model.setEarningPrice((Integer) entry.getKey());
            model.setNum((Integer) entry.getValue());
            ticketModels.add(model);
        }
        return ticketModels;
    }

    /**
     * 根据投资金额和喊卡系数计算出投资层次列表
     *
     * @param configService
     * @param investAmount
     * @param cutFactor
     * @return
     */
    public List<Integer> getInvestLevelList(GlobalConfigService configService, int investAmount, int cutFactor) {
        int maxLevel = (int) ((float) investAmount / cutFactor);
        List<Integer> investLevelList = configService.getInvestLevelList();
        ArrayList<Integer> resultList = new ArrayList<Integer>();
        for (int i = 0; i < investLevelList.size(); i++) {
            if (investLevelList.get(i) > maxLevel) {
                break;
            }
            resultList.add(investLevelList.get(i));
        }

        return resultList;
    }

    /**
     * 根据投资层次与全局配置获得各个层次的投资分布比例
     *
     * @param configService
     * @param investLevelSize
     * @return
     */
    public List<Float> getInvestProportionList(GlobalConfigService configService, int investLevelSize) {
        HashMap<Integer, ArrayList<Float>> investProportions = configService.getInvestProportion();
        ArrayList<Float> proportionList = investProportions.get(investLevelSize);
        if (proportionList == null) {
            return new ArrayList<Float>();
        }

        return proportionList;
    }

    /**
     * 计算每期的项目收益
     *
     * @param InstallmentNum 期数
     * @return 0:小R每期 1:小R最后一期 2:大R每期 3:大R 最后一期
     */
    public List CalculateActivityEarnings(int targetFund, int InstallmentNum, int srInvestProportion, int brInvestProportion) {
        List list = new ArrayList();
        int srInvestProportionLines = srInvestProportion;
        int brInvestProportionLines = brInvestProportion;

        int temp = targetFund - (srInvestProportionLines + brInvestProportionLines);
        srInvestProportionLines += temp;

        int Balance = srInvestProportionLines % InstallmentNum;
        int TerminallyLines = (srInvestProportionLines - Balance) / InstallmentNum;
        list.add(TerminallyLines);
        list.add(TerminallyLines + Balance);

        int brBalance = brInvestProportionLines % InstallmentNum;
        int brTerminallyLines = (brInvestProportionLines - Balance) / InstallmentNum;
        list.add(brTerminallyLines);
        list.add(brTerminallyLines + brBalance);

        return list;
    }

    /**
     * 设置项目收益
     *
     * @param ActivityID
     * @param AdvanceNum
     * @param PurchaseNum
     * @param LinesEarnings       小R 收益金额
     * @param LinePeoplesEarnings 大R的收益金额
     * @return
     */
    public int SetActivityInformationEarnings(final int Lines,final int LinePeoples, final String ActivityID, final int AdvanceNum,
                                              final int PurchaseNum, final String LinesEarnings, final String LinePeoplesEarnings) {
        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityVerifyCompleteModel activityVerifyCompleteModel = generaDAO.getActivityVerifyCompleteModelNoTransaction(ActivityID);
                if ( activityVerifyCompleteModel == null) {
                    return false;
                }

                activityVerifyCompleteModel.setTotalLinePeoples( LinePeoples );
                activityVerifyCompleteModel.setTotalLines( Lines );

                List<SREarningModel> LinesSREarningList = GsonUntil.jsonToJavaClass( LinesEarnings,new TypeToken<List<SREarningModel>>(){}.getType());
                List<SREarningModel> LinePeoplesSREarningList = GsonUntil.jsonToJavaClass( LinePeoplesEarnings,new TypeToken<List<SREarningModel>>(){}.getType());
                String linesPeoples = LinePeoplesEarnings.replaceAll("\r|\n|\\s*", "");
                activityVerifyCompleteModel.setEarningPeoples( linesPeoples );
                generaDAO.saveOrupdateNoTransaction(activityVerifyCompleteModel);

                //小R发奖
                for (SREarningModel LinesSREarning : LinesSREarningList) {
                    SREarningModel newSREarningModel = new SREarningModel(LinesSREarning);
                    newSREarningModel.setActivityVerifyCompleteModel(activityVerifyCompleteModel);
                    generaDAO.saveOrupdateNoTransaction(newSREarningModel);
                }

                int PurchaseNumIndex = 0;
                for (int i = 0; i < AdvanceNum * PurchaseNum; i++) {
                    int Index = i + 1;
                    String InstallmentActivityID = ActivityID + "_" + Integer.toString(Index);
                    ActivityDetailModel activityDetailModel = generaDAO.getActivityDetaillNoTransaction(InstallmentActivityID);
                    if (activityDetailModel == null) {
                        return false;
                    }

                    //大R发奖
                    if (PurchaseNumIndex >= PurchaseNum) {
                        PurchaseNumIndex = 0;
                        Collections.shuffle(LinePeoplesSREarningList);
                    }

                    SREarningModel newSREarningModel = new SREarningModel(LinePeoplesSREarningList.get(PurchaseNumIndex));
                    newSREarningModel.setActivityDetailModel(activityDetailModel);
                    generaDAO.saveOrupdateNoTransaction(newSREarningModel);
                    PurchaseNumIndex++;
                }
                return true;
            }
        });
        return 0;
    }
}
