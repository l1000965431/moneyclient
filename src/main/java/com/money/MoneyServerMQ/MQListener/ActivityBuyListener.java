package com.money.MoneyServerMQ.MQListener;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.Map;

/**
 * Created by liumin on 15/7/13.
 */


public class ActivityBuyListener extends MoneyServerListener {

    @Autowired
    private PurchaseInAdvance purchaseInAdvance;

    @Autowired
    private ActivityService activityService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        try {
            String messageBody = BodyToString(message.getBody());
            Map<String,Object> map;
            map = GsonUntil.jsonToJavaClass(messageBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if( map == null ){
                return Action.CommitMessage;
            }

            final String InstallmentActivityID = (String)map.get( "InstallmentActivityID" );
            final String UserID = (String)map.get( "UserID" );
            int PurchaseNum = (Integer)map.get( "PurchaseNum" );
            int AdvanceNum = (Integer)map.get( "AdvanceNum" );
            int PurchaseType = (Integer)map.get( "PurchaseType" );
            //String OrderID = (String)map.get( "OrderID" );

            //修改订单状态 修改项目参与人数 修改项目当前金额
            ActivityBuy( InstallmentActivityID,UserID,PurchaseNum,AdvanceNum,PurchaseType,"" );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }

    /**
     * 项目购买
     * @param InstallmentActivityID
     * @param UserID
     * @param PurchaseNum
     * @param AdvanceNum
     * @param OrderID
     * @throws Exception
     */
    public void ActivityBuy( String InstallmentActivityID,String UserID,int PurchaseNum ,int AdvanceNum,int PurchaseType,String OrderID ) throws Exception {
        switch( PurchaseType ){
            case Config.PURCHASELOCALTYRANTS:
                purchaseInAdvance.PurchaseInAdvance( InstallmentActivityID, UserID,PurchaseNum,AdvanceNum );
            case Config.PURCHASEPRICKSILK:
                purchaseInAdvance.LocalTyrantsPurchaseActivity( InstallmentActivityID,UserID,AdvanceNum );
        }

        //项目购买完成 开始计算开奖
        activityService.SetInstallmentActivityEnd( InstallmentActivityID );
    }
}
