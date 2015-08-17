package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.activity.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.Map;

/**
 * Created by liumin on 15/8/13.
 */


public class StartInstallmentActivityListener extends MoneyServerListener {

    @Autowired
    ActivityService activityService;

    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String body = BodyToString(message.getBody());
            Map<String,Object> map = GsonUntil.jsonToJavaClass( body,new TypeToken<Map<String,Object>>(){}.getType() );

            String ActivityID = map.get( "ActivityID" ).toString();
            int Installment = (Integer)map.get( "Installment" );
            activityService.InstallmentActivityStart( ActivityID,Installment );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }

}
