package com.money.MoneyServerMQ.MQListener;

import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.dao.BaseDao;
import com.money.dao.GeneraDAO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liumin on 15/7/13.
 */

public class ActivityBuyListener extends MoneyServerListener {

    @Autowired
    private GeneraDAO baseDao;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        try {
            String messagebody = BodyToString(message.getBody());

            //修改订单状态 修改项目参与人数 修改项目当前金额
            //baseDao.excuteTransactionBySQL();
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
