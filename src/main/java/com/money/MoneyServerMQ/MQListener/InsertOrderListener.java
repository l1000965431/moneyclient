package com.money.MoneyServerMQ.MQListener;

import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.dao.GeneraDAO;
import com.money.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import until.GsonUntil;

/**
 * Created by liumin on 15/7/10.
 */

public class InsertOrderListener extends MoneyServerListener {

    @Autowired
    private GeneraDAO baseDao;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String bodyString = BodyToString( message.getBody() );
            OrderModel orderModel = GsonUntil.jsonToJavaClass(bodyString, OrderModel.class);
            baseDao.save( orderModel );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
