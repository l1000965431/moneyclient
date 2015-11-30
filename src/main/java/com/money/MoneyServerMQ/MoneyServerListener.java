package com.money.MoneyServerMQ;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;

/**
 * 服务器消息监听  监听者的consume必须是无状态的
 * <p>User: seele
 * <p>Date: 15-7-9 下午5:50
 * <p>Version: 1.0
 */


public class MoneyServerListener implements MessageListener {

    public String getMessageTopic() {
        return MessageTopic;
    }

    public void setMessageTopic(String messageTopic) {
        MessageTopic = messageTopic;
    }

    String MessageTopic;

    public Action consume(Message message, ConsumeContext consumeContext) {
        return null;
    }

    public String BodyToString( byte[] body ) throws Exception{
        String stringbody = new String(body,"UTF-8");
        return stringbody;
    }

}
