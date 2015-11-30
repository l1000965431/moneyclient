package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import until.GsonUntil;
import until.UmengPush.UMengPush;
import until.UmengPush.UmengSendParameter;

import java.util.Map;

/**
 * Created by liumin on 15/8/11.
 */


public class PushListener extends MoneyServerListener {

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String body = BodyToString(message.getBody());
            UmengSendParameter mapBody = GsonUntil.jsonToJavaClass(body, UmengSendParameter.class);
            UMengPush.CustomizedcastSendMessage( mapBody );

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
    }
}
