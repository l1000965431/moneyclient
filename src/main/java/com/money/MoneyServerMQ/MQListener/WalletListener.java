package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.Wallet.WalletService;
import com.money.config.MoneyServerMQ_Topic;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;
import until.UmengPush.UmengSendParameter;

import java.util.Map;

/**
 * Created by liumin on 15/8/10.
 */

public class WalletListener extends MoneyServerListener {

    @Autowired
    WalletService walletService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String UserID = null;
        try {
            String MessageBody =  BodyToString( message.getBody() );
            Map<String,Object> map = GsonUntil.jsonToJavaClass( MessageBody,new TypeToken<Map<String,Object>>(){}.getType());

            if( map == null ){
                return Action.CommitMessage;
            }

            Map<String,Object> mapdata = (Map)map.get( "data" );
            Map<String,Object> mapobject = (Map)mapdata.get( "object" );

            String metadataJson = mapobject.get( "metadata" ).toString();
            Map<String,Object> mapMetadata = (Map)mapobject.get( "metadata" );

            if( mapMetadata == null ){
                return Action.CommitMessage;
            }

            UserID = mapMetadata.get( "UserID" ).toString();
            int Lines = (Integer)map.get( "amount" )/100;
            String OrderID = map.get( "order_no" ).toString();
            String ChannelID = map.get( "channel" ).toString();

            walletService.RechargeWallet( UserID,Lines);
            walletService.InsertWalletOrder(OrderID, Lines, ChannelID);

            UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","微距竞投","充值成功,成功充入"+Integer.toString(Lines)+"元","充值成功" );
            String Json = GsonUntil.JavaClassToJson( umengSendParameter );
            MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"1"));

            return Action.CommitMessage;
        } catch (Exception e) {
            if( UserID != null ){
                UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","微距竞投","亲,服务器热爆了,请稍后再重试一下。","充值失败" );
                String Json = GsonUntil.JavaClassToJson( umengSendParameter );
                MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"1"));
            }

            return Action.CommitMessage;
        }
    }
}
