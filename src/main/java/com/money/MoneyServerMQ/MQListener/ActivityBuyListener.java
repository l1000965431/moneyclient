package com.money.MoneyServerMQ.MQListener;

import com.money.MoneyServerMQ.MoneyServerListener;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
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

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        try {
            String messagebody = BodyToString(message.getBody());

            final boolean IsActivityGroup = false;

            final int activityID  = 0;

            final int lines = 0;

            final int orderID = 0;

            //修改订单状态 修改项目参与人数 修改项目当前金额
            baseDao.excuteTransactionByCallback(new TransactionCallback() {
                public void callback(BaseDao basedao) throws Exception {
                    ActivityBuy( IsActivityGroup,activityID,lines,orderID );
                }
            });

            return Action.CommitMessage;
        } catch (Exception e) {
            return Action.CommitMessage;
        }
    }


    /**
     * 项目购买
     * @param IsActivityGroup  是否是项目组
     * @param ActivityID       项目ID
     * @param Lines            金额
     */
    public void ActivityBuy( boolean IsActivityGroup,int ActivityID,int Lines,int OrderID ) throws Exception {

        if( IsActivityGroup ){
            //验证项目是否完成

            //验证单个金额人数是否已满

            //订单修改 插入购买表中


        }else{
            ActivityService activityService = ServiceFactory.getService( "ActivityService" );
            if( activityService == null ){
                return;
            }

            OrderService orderService = ServiceFactory.getService( "OrderService" );
            if( orderService == null ){
                return;
            }

            ActivityDetailModel activityDetailModel = activityService.getActivityDetails( ActivityID );
            ActivityDynamicModel activityDynamicModel = activityService.getActivityDynamic( ActivityID );

            if( activityDetailModel == null || activityDynamicModel == null ){
                return;
            }

            //验证项目是否完成
            if( activityDetailModel.getStatus() == ActivityDetailModel.ONLINEACTIVITY_COMPLETE ){
                return;
            }

            //验证单个金额人数是否已满
            if( activityService.IsActivityLinesPeoplesFull( activityDetailModel,activityDynamicModel,Lines ) ){
                return;
            }

            //订单修改 插入购买表中
            orderService.submitteOrder( OrderID );
            //增加人数和金额对应的人数
            activityService.AddActivityLines( activityDynamicModel,Lines );
            activityService.AddActivityCurLinesPeoples( activityDynamicModel,Lines );
            //检测是否项目完成
            activityService.SetActivityCompelete( activityDetailModel,activityDynamicModel );
            baseDao.update( activityDetailModel );
            baseDao.update( activityDynamicModel );
        }
    }
}
