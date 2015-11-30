package com.money.MoneyServerMQ.MQListener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.MoneyServerMQ.MoneyServerListener;
import com.money.Service.InviteCodeService.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liumin on 15/10/4.
 */
public class InsertInviteCodeListener  extends MoneyServerListener {

    @Autowired
    private InviteCodeService inviteCodeService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            inviteCodeService.AddInviteCode( 3000 );
            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
    }

}
