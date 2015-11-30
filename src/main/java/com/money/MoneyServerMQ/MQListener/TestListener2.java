package com.money.MoneyServerMQ.MQListener;

import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;

/**
 * Created by liumin on 15/7/9.
 */
public class TestListener2 extends MoneyServerListener {

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        System.out.println(message);
        return Action.CommitMessage;
    }

}
