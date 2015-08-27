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
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import org.hibernate.Session;
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

    @Autowired
    GeneraDAO generaDAO;

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
            Map<String,Object> mapMetadata = (Map)mapobject.get( "metadata" );

            if( mapMetadata == null ){
                return Action.CommitMessage;
            }

            UserID = mapMetadata.get( "UserID" ).toString();
            Double nLinse = (Double)mapobject.get( "amount" );
            final int Lines = (nLinse.intValue()/100);
            final String OrderID = mapobject.get( "order_no" ).toString();
            final String ChannelID = mapobject.get( "channel" ).toString();

            final String finalUserID = UserID;
            if(generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
                public boolean callback(Session session) throws Exception {
                    if(walletService.RechargeWallet(finalUserID,Lines) == 0){
                        return false;
                    }
                    walletService.InsertWalletOrder(OrderID, Lines, ChannelID);
                    return true;
                }
            })!= Config.SERVICE_SUCCESS){
                return Action.CommitMessage;
            }

            UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","充值成功","充值成功,成功充入"+Integer.toString(Lines)+"元","充值成功" );
            String Json = GsonUntil.JavaClassToJson( umengSendParameter );
            MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"充值成功"));

            return Action.CommitMessage;
        } catch (Exception e) {
            if( UserID != null ){
                UmengSendParameter umengSendParameter = new UmengSendParameter( UserID,"微距竞投","充值失败","你的充值遇到了问题请重新操作","充值失败" );
                String Json = GsonUntil.JavaClassToJson( umengSendParameter );
                MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG,Json,"充值失败"));
            }

            return Action.CommitMessage;
        }
    }
}
