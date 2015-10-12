package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.mchange.v2.resourcepool.TimeoutException;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.activity.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.Map;

/**
 * Created by liumin on 15/10/4.
 */
public class StartInstallmentActivityTestListener extends MoneyServerListener {

    @Autowired
    ActivityService activityService;

    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String body = BodyToString(message.getBody());
            Map<String,Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
            }.getType());

            String ActivityID = map.get( "ActivityID" ).toString();
            Double Installment = (Double)map.get( "Installment" );
            activityService.InstallmentActivityStartTest( ActivityID,Installment.intValue() );
            return Action.CommitMessage;
        }catch ( TimeoutException e ){
            return Action.ReconsumeLater;
        }
        catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
