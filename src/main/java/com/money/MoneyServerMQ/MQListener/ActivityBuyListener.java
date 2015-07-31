package com.money.MoneyServerMQ.MQListener;

import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.money.Service.PurchaseInAdvance.PurchaseInAdvance;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.order.OrderService;
import com.money.dao.BaseDao;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionCallback;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liumin on 15/7/13.
 */

public class ActivityBuyListener extends MoneyServerListener {

    @Autowired
    private GeneraDAO baseDao;

    private PurchaseInAdvance purchaseInAdvance;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        try {
            String messagebody = BodyToString(message.getBody());

            final int activityID  = 0;

            final int lines = 0;

            final int orderID = 0;

            //修改订单状态 修改项目参与人数 修改项目当前金额

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }

    /**
     * 项目购买
     * @param InstallmentActivityID       分期项目ID
     * @param Lines                       金额
     * @param OrderID                     订单ID
     */
    public void ActivityBuy( int InstallmentActivityID,int Lines,int OrderID ) throws Exception {


        //purchaseInAdvance.




    }
}
