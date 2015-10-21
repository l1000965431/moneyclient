package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

import java.util.List;
import java.util.Map;

/**
 * Created by liumin on 15/10/15.
 */

public class JoinActivityPreferentialListener extends MoneyServerListener {

    @Autowired
    ActivityPreferentialService activityPreferentialService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String bodyString = BodyToString(message.getBody());
            Map<String, Object> map = GsonUntil.jsonToJavaClass(bodyString, new TypeToken<Map<String, Object>>() {
            }.getType());
            String userId = map.get("userId").toString();
            int userExp = Integer.valueOf(map.get("uerExp").toString());
            int AcitivityId = Integer.valueOf(map.get("activityId").toString());
            activityPreferentialService.JoinActivityPreferential(AcitivityId, userId, userExp );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
    }

}
