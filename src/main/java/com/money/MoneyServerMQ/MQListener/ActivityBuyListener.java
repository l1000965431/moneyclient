package com.money.MoneyServerMQ.MQListener;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;
import until.UmengPush.UmengSendParameter;

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
            Map<String,String> map;
            map = GsonUntil.jsonToJavaClass(messageBody, new TypeToken<Map<String, String>>() {
            }.getType());

            if( map == null ){
                return Action.CommitMessage;
            }

            final String InstallmentActivityID = map.get( "InstallmentActivityID" );
            final String UserID = map.get( "UserID" );
            int PurchaseNum = Integer.valueOf( map.get( "PurchaseNum" ));
            int AdvanceNum = Integer.valueOf( map.get( "AdvanceNum" ));
            int PurchaseType = Integer.valueOf( map.get( "PurchaseType" ) );
            String OrderID = map.get( "OrderID" ).toString();

            //修改订单状态 修改项目参与人数 修改项目当前金额
            ActivityBuy( InstallmentActivityID,UserID,PurchaseNum,AdvanceNum,PurchaseType,OrderID );
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

        StringBuffer ActivityName = new StringBuffer();
        int Result = 0;
        switch( PurchaseType ){
            case Config.PURCHASEPRICKSILK:
                Result = purchaseInAdvance.PurchaseInAdvance( InstallmentActivityID, UserID,PurchaseNum,AdvanceNum,OrderID,ActivityName );
                break;
            case Config.PURCHASELOCALTYRANTS:
                Result = purchaseInAdvance.LocalTyrantsPurchaseActivity( InstallmentActivityID,UserID,AdvanceNum,OrderID,ActivityName );
                break;
        }

        if( Result == 1){
            //项目购买完成 开始计算开奖
            activityService.SetInstallmentActivityEnd( InstallmentActivityID );
            //发送成功入资的消息
            UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","购买成功","您已成功入资"+ActivityName.toString()+"项目","购买成功" );
            String Json = GsonUntil.JavaClassToJson( umengSendParameter );
            MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"购买成功"));
        }else{
            //发送失败的消息
            if( ActivityName == null || ActivityName.length() == 0 ){
                UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","购买错误","购买失败,请重新操作","购买错误" );
                String Json = GsonUntil.JavaClassToJson( umengSendParameter );
                MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"购买错误"));
            }else{
                UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","购买失败","您入资的"+ActivityName.toString()+"项目款项已满。资金已退回。您可继续需关注该项目的其他动态或查看。","购买失败" );
                String Json = GsonUntil.JavaClassToJson( umengSendParameter );
                MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"购买失败"));
            }


        }

    }
}
