package com.money.Service.GroupActivity;

import com.money.Service.GlobalConifg.GlobalConfigService;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceFactory;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by happysky on 15-7-15.
 */
@Service("ServiceGroupActivity")
public class ServiceGroupActivity extends ServiceBase implements ServiceInterface {
    @Autowired
    private GeneraDAO generaDAO;

    /**
     *  设置项目大小R比例
     */
    public void setActivityInvestProportion(String activityId, float srProportion, float brProportion){
        ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel)generaDAO.load(ActivityVerifyCompleteModel.class, activityId);
        if( completeModel == null ){
            return;
        }

        completeModel.setSrInvestProportion(srProportion);
        completeModel.setBrInvestProportion(brProportion);
        generaDAO.update(completeModel);
    }

    public void splitActivityByStage(){

        ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel)generaDAO.load(ActivityVerifyCompleteModel.class, "4");

        HashSet<ActivityDetailModel> activityDetailModels = new HashSet<ActivityDetailModel>();
        HashSet<ActivityDynamicModel> activityDynamicModels = new HashSet<ActivityDynamicModel>();

        for( int i = 0; i < 4; i++ ){
            ActivityDetailModel activityDetailModel = new ActivityDetailModel();
            ActivityDynamicModel activityDynamicModel = new ActivityDynamicModel();

            activityDetailModel.setActivityStageId( completeModel.getActivityId() + "_" + String.valueOf(i));
            activityDetailModel.setTargetFund( 25000 );
            activityDetailModel.setActivityVerifyCompleteModel(completeModel);
            activityDetailModel.setDynamicModel(activityDynamicModel);

            activityDynamicModel.setActivityStageId( activityDetailModel.getActivityStageId() );
            activityDynamicModel.setActivityDetailModel(activityDetailModel);
            activityDynamicModel.setActivityVerifyCompleteModel(completeModel);

            activityDetailModels.add(activityDetailModel);
            activityDynamicModels.add(activityDynamicModel);
        }

        completeModel.setActivityDetailModels( activityDetailModels );
        completeModel.setActivityDynamicModels( activityDynamicModels );
        generaDAO.update(completeModel);
    }

    /**
     * 创建一个项目组
     * @param name
     * @return
     */
    public ActivityGroupModel createActivityGroup(String name, Set<ActivityDynamicModel> activityDynamicModels){
        ActivityGroupModel activityGroup = new ActivityGroupModel();
        activityGroup.setActivityDynamicModes(activityDynamicModels);
        setGroupDefaultValue(activityGroup);
        generaDAO.save(activityGroup);
        return activityGroup;
    }

    /**
     *
     */
    public void setGroupDefaultValue(ActivityGroupModel activityGroupModel){
        activityGroupModel.setInvestAmount( 100000 );
        activityGroupModel.setEarningAmount( 40000 );
        activityGroupModel.rcSetSrInvestProportion(0.5f);
        activityGroupModel.rcSetBrEarningProportion(0.65f);
    }

    /**
     * 计算投资与收益票数和小额中奖几率
     */
    public void generateGroupTickets(ActivityGroupModel activityGroupModel){
        GlobalConfigService globalConfigService = ServiceFactory.getService("GlobalConfigService");
        if( globalConfigService == null )
            return;

        int srInvestAmount = activityGroupModel.getInvestAmount();

        // 投资层次列表
        List<Integer> investLevelList = getInvestLevelList(globalConfigService, srInvestAmount,
                globalConfigService.getCutFactors().getInvestCutFactor());

        // 投资层次比例分布列表
        List<Float> investProportionList = getInvestProportionList(globalConfigService, investLevelList.size());

        // 投资每个层次金额列表
        HashSet<SRInvestTicketModel> ticketModels = calcInvestTicket(srInvestAmount, investLevelList, investProportionList);

        activityGroupModel.setSrInvestTicketModels( ticketModels );

        int ticketsNum = 0;
        for(SRInvestTicketModel model : ticketModels){
            ticketsNum += model.getNum();
        }

        HashSet<SREarningModel> prizeModels = calcEarningPrize(activityGroupModel.getEarningAmount(),  null, null, ticketsNum);

        activityGroupModel.setSrEarningModels(prizeModels);
    }

    /**
     * 根据投资金额、投资层次、与各层次比例计算出每个层次的票有多少张
     * @param investAmount
     * @param investLevelList
     * @param investProportionList
     * @return
     */
    public HashSet<SRInvestTicketModel> calcInvestTicket(int investAmount, List<Integer> investLevelList, List<Float> investProportionList ){
        HashMap<Integer, Integer> tickets = new HashMap<Integer, Integer>();
        int investPriceLeft = investAmount;
        for(int i = 0; i < investLevelList.size(); i++){
            int price = (int)(investAmount * investProportionList.get(i));
            price = price - price % investLevelList.get(i);
            investPriceLeft = investPriceLeft - price;
            tickets.put(investLevelList.get(i), price / investLevelList.get(i));
        }
        int leftMinTicket = investPriceLeft / investLevelList.get(0);
        int minTicketNum = tickets.get(investLevelList.get(0));
        tickets.put(investLevelList.get(0), leftMinTicket + minTicketNum);

        HashSet<SRInvestTicketModel> ticketModels = new HashSet<SRInvestTicketModel>();
        Iterator iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()){
            SRInvestTicketModel model = new SRInvestTicketModel();
            Map.Entry entry = (Map.Entry)iterator.next();
            model.setPrice((Integer)entry.getKey());
            model.setNum((Integer)entry.getValue());
            model.setSoldNum(0);
            ticketModels.add(model);
        }
        return ticketModels;
    }

    public HashSet<SREarningModel> calcEarningPrize(int earningAmount, List<Integer> earningLevelList, List<Float> earningProportionList, int ticketsNum){
        HashMap<Integer, Integer> tickets = new HashMap<Integer, Integer>();
        int earningPriceLeft = earningAmount;
        for(int i = 0; i < earningLevelList.size(); i++){
            int price = (int)(earningAmount * earningProportionList.get(i));
            price = price - price % earningLevelList.get(i);
            earningPriceLeft = earningPriceLeft - price;
            tickets.put(earningLevelList.get(i), price / earningLevelList.get(i));
        }
        int leftMinTicket = earningPriceLeft / earningLevelList.get(0);
        int minTicketNum = tickets.get(earningLevelList.get(0));
        tickets.put(earningLevelList.get(0), leftMinTicket + minTicketNum);

        HashSet<SREarningModel> ticketModels = new HashSet<SREarningModel>();
        Iterator iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()){
            SREarningModel model = new SREarningModel();
            Map.Entry entry = (Map.Entry)iterator.next();
            model.setEarningPrice((Integer)entry.getKey());
            model.setNum((Integer)entry.getValue());
            model.setBaseProbability( (float)model.getNum() / ticketsNum );
            ticketModels.add(model);
        }
        return ticketModels;
    }

    /**
     * 根据投资金额和喊卡系数计算出投资层次列表
     * @param configService
     * @param investAmount
     * @param cutFactor
     * @return
     */
    public List<Integer> getInvestLevelList(GlobalConfigService configService, int investAmount, int cutFactor){
        int maxLevel = (int)((float)investAmount / cutFactor);
        List<Integer> investLevelList = configService.getInvestLevelList();
        ArrayList<Integer> resultList = new ArrayList<Integer>();
        for(int i = 0; i < investLevelList.size(); i++){
            if(investLevelList.get(i) > maxLevel){
                break;
            }
            resultList.add(investLevelList.get(i));
        }

        return resultList;
    }

    /**
     * 根据投资层次与全局配置获得各个层次的投资分布比例
     * @param configService
     * @param investLevelSize
     * @return
     */
    public List<Float> getInvestProportionList(GlobalConfigService configService, int investLevelSize){
        HashMap<Integer, ArrayList<Float>> investProportions = configService.getInvestProportion();
        ArrayList<Float> proportionList = investProportions.get(investLevelSize);
        if( proportionList == null ){
            return new ArrayList<Float>();
        }

        return proportionList;
    }

    /**
     * 向组中加入一个项目
     * @param activityId
     * @return
     */
    public String addActivityToGroup(Long groupId, Long activityId){
        ActivityGroupModel activityGroupModel = (ActivityGroupModel)generaDAO.load(ActivityGroupModel.class, groupId);
        if( activityGroupModel == null ){
            return "";
        }
        return "";
    }


    public void test(){
//        ActivityGroupModel groupModel = new ActivityGroupModel();
//        groupModel.setName("group1");
//
//        ActivityDetailModel detailModel = new ActivityDetailModel();
//        detailModel.setName("123");
//
//        ActivityDetailModel detailModel1 = new ActivityDetailModel();
//        detailModel1.setName("456");
//
//        detailModel.setActivityGroupModel(groupModel);
//        detailModel1.setActivityGroupModel(groupModel);
//
//        groupModel.getActivityDetailModels().add(detailModel);
//        groupModel.getActivityDetailModels().add(detailModel1);
//
//        generaDAO.save(groupModel);

        ActivityGroupModel activityGroupModel = (ActivityGroupModel)generaDAO.load(ActivityGroupModel.class, 1l);
        if( activityGroupModel == null ){
            return;
        }
    }
}
