package com.money.Service.GroupActivity;

import com.google.gson.reflect.TypeToken;
import com.money.Service.GlobalConifg.GlobalConfigService;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.SREarningModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;

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
     *
     * @param ActicityID
     * @param AdvanceNum  多少组
     * @param PurchaseNum 一组多少个
     */
    public void splitActivityByStage1(int ntargetFund, String ActicityID, final int AdvanceNum, final int PurchaseNum) {

        final ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel) generaDAO.load(ActivityVerifyCompleteModel.class, ActicityID);

        int srInvestProportion = completeModel.getTotalLines();
        int brInvestProportion = completeModel.getTotalLinePeoples();

        final List list = CalculateActivityEarnings(ntargetFund, AdvanceNum * PurchaseNum, srInvestProportion, brInvestProportion);

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

                completeModel.setTotalInstallmentNum(AdvanceNum * PurchaseNum);
                generaDAO.updateNoTransaction(completeModel);
                return true;
            }
        });
    }


    public void splitActivityByStage(int ntargetFund, String ActicityID, final int AdvanceNum, final int PurchaseNum) {

        final ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel) generaDAO.load(ActivityVerifyCompleteModel.class, ActicityID);

        int srInvestProportion = completeModel.getTotalLines();
        int brInvestProportion = completeModel.getTotalLinePeoples();

        final List list = CalculateActivityEarnings(ntargetFund, AdvanceNum * PurchaseNum, srInvestProportion, brInvestProportion);

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                String DeSql = "insert into activitydetails " +
                        "(LocaltyrantsLotteryLines, activityEndTime, activityStartTime, activityVerifyCompleteModel_activityId, dynamicModel_activityStageId, groupId, raiseDay, stageIndex, status, targetFund, activityStageId) values ";
                String DySql = "insert into activitydynamic " +
                        "(activityCurLines, activityCurLinesPeoples, activityDetailModel_activityStageId, activityState, activityTotalAmount, activityTotalLines, activityTotalLinesPeoples, activityVerifyCompleteModel_activityId, groupId, activityStageId) values ";

                String DeVaules = "";
                String DyVaules = "";
                String ActivityID = completeModel.getActivityId();
                for (int i = 0; i < AdvanceNum * PurchaseNum; i++) {

                    String Index = Integer.toString(i + 1);
                    String ActivityStageId = ActivityID + "_" + Index;

                    //最后一期
                    int TargetFund;
                    int TotalAmount;
                    int TotalLines;
                    int TotalLinesPeoples;
                    if (i == AdvanceNum * PurchaseNum - 1) {
                        //每期总金额
                        TargetFund = (Integer) list.get(1) + (Integer) list.get(3);
                        TotalAmount = (Integer) list.get(1) + (Integer) list.get(3);
                        //每期大R小R金额
                        TotalLines = (Integer) list.get(1);
                        TotalLinesPeoples = (Integer) list.get(3);
                    } else {
                        //平常期
                        //每期总金额
                        TargetFund = (Integer) list.get(0) + (Integer) list.get(2);
                        TotalAmount = (Integer) list.get(0) + (Integer) list.get(2);

                        //每期大R小R金额
                        TotalLines = (Integer) list.get(0);
                        TotalLinesPeoples = (Integer) list.get(2);
                    }

                    int GroupId = (i / PurchaseNum) + 1;
                    String StartTime = MoneyServerDate.getStringCurDate();
                    int StageIndex = i + 1;

                    String Detemp = "(LocaltyrantsLotteryLines,'activityEndTime','activityStartTime','activityVerifyCompleteModel_activityId','dynamicModel_activityStageId',groupId,raiseDay,stageIndex,status,targetFund,'activityStageId'),";
                    Detemp = Detemp.replace("LocaltyrantsLotteryLines", "0").replace("activityEndTime", StartTime).replace("activityStartTime", StartTime).
                            replace("activityVerifyCompleteModel_activityId", ActivityID).replace("dynamicModel_activityStageId", ActivityStageId).replace("groupId", Integer.toString(GroupId)).replace("raiseDay", "0").replace("stageIndex", Integer.toString(StageIndex)).
                            replace("status", "0").replace("targetFund", Integer.toString(TargetFund)).replace("activityStageId", ActivityStageId);

                    DeVaules += Detemp;

                    String Dytemp = "(activityCurLines,CurLinesPeoples,'activityDetailModel_activityStageId',activityState,activityTotalAmount,activityTotalLines,TotalLinesPeoples,'activityVerifyCompleteModel_activityId',groupId,'activityStageId'),";
                    Dytemp = Dytemp.replace("activityCurLines", "0").replace("CurLinesPeoples", "0").replace("activityDetailModel_activityStageId", ActivityStageId).replace("activityState", "0").replace("activityTotalAmount", Integer.toString(TotalAmount)).
                            replace("activityTotalLines", Integer.toString(TotalLines)).replace("TotalLinesPeoples", Integer.toString(TotalLinesPeoples)).replace("activityVerifyCompleteModel_activityId", ActivityID).replace("groupId", Integer.toString(GroupId)).replace("activityStageId", ActivityStageId);
                    //activityDynamicModel.setActivityStageId(activityDetailModel.getActivityStageId());
                    DyVaules += Dytemp;

                    if ((i+1)%50 == 0 || i == (AdvanceNum * PurchaseNum)-1){
                        DeVaules = DeVaules.substring(0, DeVaules.length() - 1);
                        DyVaules = DyVaules.substring(0, DyVaules.length() - 1);
                        DeSql += DeVaules;
                        DySql += DyVaules;

                        SQLQuery sqlQueryde = session.createSQLQuery(DeSql);
                        SQLQuery sqlQuerydy = session.createSQLQuery(DySql);
                        SQLQuery sqlQueryfo0 = session.createSQLQuery("set @@foreign_key_checks=0;");
                        SQLQuery sqlQueryfo1 = session.createSQLQuery("set @@foreign_key_checks=1;");
                        sqlQueryfo0.executeUpdate();
                        sqlQueryde.executeUpdate();
                        sqlQuerydy.executeUpdate();
                        sqlQueryfo1.executeUpdate();

                        DeVaules = "";
                        DyVaules = "";
                        DeSql = "insert into activitydetails " +
                                "(LocaltyrantsLotteryLines, activityEndTime, activityStartTime, activityVerifyCompleteModel_activityId, dynamicModel_activityStageId, groupId, raiseDay, stageIndex, status, targetFund, activityStageId) values ";
                        DySql = "insert into activitydynamic " +
                                "(activityCurLines, activityCurLinesPeoples, activityDetailModel_activityStageId, activityState, activityTotalAmount, activityTotalLines, activityTotalLinesPeoples, activityVerifyCompleteModel_activityId, groupId, activityStageId) values ";
                    }

                }

                completeModel.setTotalInstallmentNum(AdvanceNum * PurchaseNum);
                generaDAO.updateNoTransaction(completeModel);
                return true;
            }
        });
    }

    public HashSet<SREarningModel> calcEarningPrize(int earningAmount, List<Integer> earningLevelList, List<Float> earningProportionList, int ticketsNum) {
        HashMap<Integer, Integer> tickets = new HashMap();
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
        for (Object o : tickets.entrySet()) {
            SREarningModel model = new SREarningModel();
            Map.Entry entry = (Map.Entry) o;
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
        ArrayList<Integer> resultList = new ArrayList();
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

        int temp = targetFund - (srInvestProportionLines + brInvestProportion);
        srInvestProportionLines += temp;

        int Balance = srInvestProportionLines % InstallmentNum;
        int TerminallyLines = (srInvestProportionLines - Balance) / InstallmentNum;
        list.add(TerminallyLines);
        list.add(TerminallyLines + Balance);

        int brBalance = brInvestProportion % InstallmentNum;
        int brTerminallyLines = (brInvestProportion - Balance) / InstallmentNum;
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
    public int SetActivityInformationEarnings(final int Lines, final int LinePeoples, final String ActivityID, final int AdvanceNum,
                                              final int PurchaseNum, final String LinesEarnings, final String LinePeoplesEarnings) {
        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityVerifyCompleteModel activityVerifyCompleteModel = generaDAO.getActivityVerifyCompleteModelNoTransaction(ActivityID);
                if (activityVerifyCompleteModel == null) {
                    return false;
                }

                activityVerifyCompleteModel.setTotalLinePeoples(LinePeoples);
                activityVerifyCompleteModel.setTotalLines(Lines);

                List<SREarningModel> LinesSREarningList = GsonUntil.jsonToJavaClass(LinesEarnings, new TypeToken<List<SREarningModel>>() {
                }.getType());
                List<SREarningModel> LinePeoplesSREarningList = GsonUntil.jsonToJavaClass(LinePeoplesEarnings, new TypeToken<List<SREarningModel>>() {
                }.getType());

                if( LinesSREarningList == null || LinePeoplesSREarningList == null ){
                    return false;
                }


                String linesPeoples = LinePeoplesEarnings.replaceAll("\r|\n|\\s*", "");
                activityVerifyCompleteModel.setEarningPeoples(linesPeoples);
                generaDAO.saveOrupdateNoTransaction(activityVerifyCompleteModel);

                //小R发奖
                String sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                String Vaules = "";
                int rIndex = 0;
                for (SREarningModel LinesSREarning : LinesSREarningList) {
                    SREarningModel newSREarningModel = new SREarningModel(LinesSREarning);
                    newSREarningModel.setActivityVerifyCompleteModel(activityVerifyCompleteModel);

                    String temp = "('activityStageId', 'activityId', earningPrice, earningType, num),";
                    temp = temp.replace( "activityStageId","" ).replace( "activityId",activityVerifyCompleteModel.getActivityId() ).
                            replace("earningPrice", Integer.toString(LinesSREarning.getEarningPrice())).replace("earningType", Integer.toString(LinesSREarning.getEarningType())).
                            replace("num",Integer.toString(LinesSREarning.getNum()));
                    Vaules += temp;
                    rIndex++;
                    if( rIndex%50 ==0 || rIndex == LinesSREarningList.size() ){
                        Vaules = Vaules.substring( 0,Vaules.length()-1 );
                        sql += Vaules;
                        String sql0 = "set @@foreign_key_checks=0; ";
                        String sql1 = "set @@foreign_key_checks=1; ";
                        generaDAO.getNewSession().createSQLQuery( sql0 ).executeUpdate();
                        generaDAO.getNewSession().createSQLQuery( sql ).executeUpdate();
                        generaDAO.getNewSession().createSQLQuery( sql1 ).executeUpdate();

                        sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                        Vaules = "";
                    }
                }

                sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                Vaules = "";
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

                    String temp = "('activityStageId', 'activityId', earningPrice, earningType, num),";
                    temp = temp.replace( "activityStageId",InstallmentActivityID ).replace( "activityId","" ).
                            replace("earningPrice", Integer.toString(LinePeoplesSREarningList.get(PurchaseNumIndex).getEarningPrice())).replace("earningType", Integer.toString(LinePeoplesSREarningList.get(PurchaseNumIndex).getEarningType())).
                            replace("num", Integer.toString(LinePeoplesSREarningList.get(PurchaseNumIndex).getNum()));
                    Vaules += temp;

                    if( Index%50 ==0 || i == (AdvanceNum * PurchaseNum)-1 ){
                        Vaules = Vaules.substring( 0,Vaules.length()-1 );
                        sql += Vaules;
                        String sql0 = "set @@foreign_key_checks=0; ";
                        String sql1 = "set @@foreign_key_checks=1; ";
                        generaDAO.getNewSession().createSQLQuery( sql0 ).executeUpdate();
                        generaDAO.getNewSession().createSQLQuery( sql ).executeUpdate();
                        generaDAO.getNewSession().createSQLQuery( sql1 ).executeUpdate();

                        sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                        Vaules = "";
                    }

                    PurchaseNumIndex++;
                }
                return true;
            }
        });
        return 0;
    }
}
