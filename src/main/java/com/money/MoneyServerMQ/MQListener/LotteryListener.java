package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.Lottery.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liumin on 15/7/31.
 */

public class LotteryListener extends MoneyServerListener {

    @Autowired
    private LotteryService lotteryService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String InstallmentActivityID = BodyToString( message.getBody() );
            lotteryService.StartLottery( InstallmentActivityID );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }
}
