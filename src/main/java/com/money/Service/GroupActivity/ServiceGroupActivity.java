package com.money.Service.GroupActivity;

import com.money.Service.GlobalConifg.GlobalConfigService;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceFactory;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.model.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.MoneyServerDate;

import java.util.*;

/**
 * Created by happysky on 15-7-15.
 */
@Service("ServiceGroupActivity")
public class ServiceGroupActivity extends ServiceBase implements ServiceInterface {
    @Autowired
    private GeneraDAO generaDAO;

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
     */
    public void splitActivityByStage(String ActicityID, final int AdvanceNum, final int PurchaseNum) {

        final ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel) generaDAO.load(ActivityVerifyCompleteModel.class, ActicityID);

        int targetFund = completeModel.getTargetFund();
        float srInvestProportion = completeModel.getSrInvestProportion();
        float brInvestProportion = completeModel.getBrInvestProportion();

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
                    activityDetailModel.setGroupId((i % PurchaseNum) + 1);
                    activityDetailModel.setActivityStartTime(MoneyServerDate.getDateCurDate());

                    activityDynamicModel.setActivityStageId(activityDetailModel.getActivityStageId());
                    activityDynamicModel.setActivityVerifyCompleteModel(completeModel);
                    activityDynamicModel.setGroupId((i % PurchaseNum) + 1);

                    activityDynamicModel.setActivityDetailModel(activityDetailModel);
                    activityDetailModel.setDynamicModel(activityDynamicModel);

                    generaDAO.saveNoTransaction( activityDetailModel );
                }
                return true;
            }
        });

        //completeModel.setActivityDetailModels( activityDetailModels );
        //completeModel.setActivityDynamicModels( activityDynamicModels );
        //generaDAO.update(completeModel);
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
    public List CalculateActivityEarnings(int targetFund, int InstallmentNum, float srInvestProportion, float brInvestProportion) {
        List list = new ArrayList();
        int srInvestProportionLines = (int) (targetFund * srInvestProportion);
        int brInvestProportionLines = (int) (targetFund * brInvestProportion);

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
}
