package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.Wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.Map;

/**
 * Created by liumin on 15/8/26.
 */
public class TransListener extends MoneyServerListener {

    @Autowired
    WalletService walletService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        String MessageBody;
        try {
            MessageBody = BodyToString(message.getBody());
            Map<String, Object> map = GsonUntil.jsonToJavaClass(MessageBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if (map == null) {
                return Action.CommitMessage;
            }

            Map<String,Object> mapdata = (Map)map.get( "data" );
            Map<String,Object> mapobject = (Map)mapdata.get( "object" );

            String status = mapobject.get( "status" ).toString();
            double ammont = Double.valueOf(mapobject.get( "amount" ).toString())/100.0;
            String openId = mapobject.get( "recipient" ).toString();
            String orderId = mapobject.get( "transaction_no" ).toString();
            if( status.equals( "paid" ) ){
                walletService.TransferLines( orderId,openId,(int)ammont,status );
            }

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
