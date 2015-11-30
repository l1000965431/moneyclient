package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.MoneyServerMQ.MoneyServerListener;
import until.GsonUntil;
import until.UmengPush.UMengMessage;
import until.UmengPush.UMengPush;
import until.UmengPush.UmengSendParameter;

/**
 * Created by liumin on 15/10/15.
 */

public class PushCustomMessageListener extends MoneyServerListener {

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try{
            String body = BodyToString(message.getBody());

            UMengMessage uMengMessage = GsonUntil.jsonToJavaClass(body, UMengMessage.class);

            UmengSendParameter umengSendParameter = new UmengSendParameter(uMengMessage);
            UMengPush.CustomizedcastSendMessage(umengSendParameter);

            return Action.CommitMessage;
        }catch ( Exception e ){

            return Action.CommitMessage;
        }
    }

}
