package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.Wallet.WalletService;
import com.money.dao.PrizeListDAO.PrizeListDAO;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.LotteryPeoples;
import com.money.model.PrizeListModel;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.List;
import java.util.Map;

/**
 * 给客户发奖打钱到客户钱包
 * <p>User: seele
 * <p>Date: 15-7-31
 * <p>Version: 1.0
 */


public class SomeFarmListener extends MoneyServerListener {

    @Autowired
    WalletService walletService;

    @Autowired
    activityDAO activityDAO;

    @Autowired
    PrizeListDAO prizeListDAO;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String InstallmentActivityID = BodyToString( message.getBody() );
            ActivityDetailModel activityDetailModel = activityDAO.getActivityDetails( InstallmentActivityID );

            String ActivityID = activityDetailModel.getActivityVerifyCompleteModel().getActivityId();
            int GroupID = activityDetailModel.getGroupId();

            List<ActivityDetailModel> list = activityDAO.getActivityDetailByGroupID( ActivityID,GroupID );

            if( list == null ){
                return Action.CommitMessage;
            }

            SomeFarmByPrizeList( list );

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }

    /**
     * 给人钱包充值
     * @param list
     */
    void SomeFarmByPrizeList( List<ActivityDetailModel> list ){
        for( ActivityDetailModel it : list ){
            String ActivityStageId = it.getActivityStageId();

            PrizeListModel prizeListModel = prizeListDAO.getListPrizeListModel(ActivityStageId);

            String json = prizeListModel.getPrizeSituation();
            List<LotteryPeoples> LotteryPeoplesList = GsonUntil.jsonToJavaClass( json,new TypeToken<List<LotteryPeoples>>(){}.getType() );

            if( LotteryPeoplesList == null ){
                continue;
            }

            for( LotteryPeoples Peoples:LotteryPeoplesList ){
                walletService.RechargeWallet( Peoples.getUserID(),Peoples.getLotteryLines() );
            }

        }

    }

}
