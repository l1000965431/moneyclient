package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.model.LotteryPeoples;
import until.GsonUntil;
import until.UmengPush.UMengPush;
import until.UmengPush.UmengSendParameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by liumin on 15/8/25.
 */
public class LotteryListPushListener extends MoneyServerListener {


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String body = BodyToString(message.getBody());
            Map<String, Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
            }.getType());
            Iterator entries = map.entrySet().iterator();
            List<String> TempList = new ArrayList();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String ActivityName = (String) entry.getKey();
                List<Map<String,Object>> value = (List<Map<String, Object>>) entry.getValue();

                for (Map<String,Object> it : value) {
                    UmengSendParameter umengSendParameter = new UmengSendParameter(it.get( "UserId" ).toString(),
                            "微距竞投", "项目收益", "你入资的" + ActivityName + "项目为你创造了" + it.get("LotteryLines").toString() + "的收益", "项目收益");
                    UMengPush.CustomizedcastSendMessage(umengSendParameter);

                    if (!TempList.contains(it.get( "UserId" ).toString())) {
                        TempList.add(it.get( "UserId" ).toString());
                    }
                }
            }

            for (String userid : TempList) {
                UmengSendParameter umengSendParameter = new UmengSendParameter(userid, "redpoint", "收益通知红点");

                UMengPush.CustomizedcastSendMessage(umengSendParameter);
            }
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
    }

}
