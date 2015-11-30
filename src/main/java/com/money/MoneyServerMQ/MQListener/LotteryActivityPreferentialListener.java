package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import com.money.config.MoneyServerMQ_Topic;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;
import until.UmengPush.UMengMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/10/15.
 */
public class LotteryActivityPreferentialListener extends MoneyServerListener {

    @Autowired
    ActivityPreferentialService activityPreferentialService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String bodyString = BodyToString(message.getBody());
            Map<String, String> map = GsonUntil.jsonToJavaClass(bodyString, new TypeToken<Map<String, String>>() {
            }.getType());
            String userId = map.get("userId").toString();
            int ActivityId = Integer.valueOf(map.get("ActivityId").toString());
            int Lines = Integer.valueOf(map.get("Lines").toString());

            activityPreferentialService.ActivityPreferentialLottery( ActivityId, Lines, userId);

            Map<String,String> mapUmessagebody = new HashMap<>();
            mapUmessagebody.put( "ActivityId",Integer.toString(ActivityId) );
            mapUmessagebody.put( "Lines",Integer.toString(Lines) );
            UMengMessage uMengMessage = new UMengMessage( userId,"activityPreferentialLottery",GsonUntil.JavaClassToJson( mapUmessagebody ),"特惠项目中奖" );

            String Json = GsonUntil.JavaClassToJson(uMengMessage);
            MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_UMENGPUSHCUSTOMMESSAGE_TOPIC,
                    MoneyServerMQ_Topic.MONEYSERVERMQ_UMENGPUSHCUSTOMMESSAGE_TAG, Json, "特惠项目购中奖"));

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
    }


}
