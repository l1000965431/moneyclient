package com.money.Service.order;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.GeneraDAO;
import com.money.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.MoneySeverRandom;

import java.text.ParseException;
import java.util.List;

/**
 * 订单服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Service("OrderService")
public class OrderService extends ServiceBase implements ServiceInterface {

    @Autowired
    private GeneraDAO baseDao;

    //订单未提交
    final static int ORDER_STATE_NOSUBMITTED = 0;

    //订单正在提交
    final static int ORDER_STATE_SUBMITTING = 1;

    //订单提交成功
    final static int ORDER_STATE_SUBMITTSUCCESS = 2;

    //订单提交失败
    final static int ORDER_STATE_SUBMITTEDFAIL = 3;

    //订单取消
    final static int ORDER_STATE_SUBMITTECANEL = 4;

    OrderService() {
        super();
    }

    /**
     * 生成订单
     *
     *@param userID
     * 用户ID
     *
     * @param activityID
     * 项目ID
     *
     * @return
     */
    public String createOrder( int userID,int activityID,int lines,int activitygroupID ){

        Long OrderID = createOrderID();

        OrderModel orderModel = new OrderModel();

        orderModel.setActivitygroupid(activitygroupID);
        orderModel.setActivityid(activityID);
        orderModel.setOrderlines(lines);
        orderModel.setUserid(userID);
        orderModel.setOrderid(OrderID);
        try {
            orderModel.setOrderdate( MoneyServerDate.getDateCurDate());
        } catch (ParseException e) {
            return null;
        }
        orderModel.setOrderstate(ORDER_STATE_NOSUBMITTED);

        //插入消息队列
        String messagebody = GsonUntil.JavaClassToJson( orderModel );
        MoneyServerMQManager.SendMessage( new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_ORDERINSERT_TAG,messagebody,Long.toString(OrderID)) );

        return "SUCCESS";
    }

    /**
     *
     * 取消订单
     *
     * @return
     */
    public String cancelOrder( long orderID ){

        OrderModel orderModel = getOrderByOrderID( orderID );

        if( orderModel == null ){
            return Config.SERVICE_FAILED;
        }

        orderModel.setOrderstate( ORDER_STATE_SUBMITTECANEL );
        baseDao.save( orderModel );

        return Config.SERVICE_SUCCESS;
    }


    /**
     *
     * 删除订单
     *
     * @return
     */
    public String deleteOrder( long OrderID ){
        OrderModel orderModel = getOrderByOrderID( OrderID );
        try{
            baseDao.delete( orderModel );
            return Config.SERVICE_SUCCESS;
        }catch ( Exception e ){
            return Config.SERVICE_FAILED;
        }
    }


    /**
     *
     * 删除订单
     *
     * @param OrderID
     * 订单ID
     *
     * @return
     * 返回对应的订单
     */

    public OrderModel getOrderByOrderID( long OrderID ){

        return (OrderModel)baseDao.load( OrderModel.class,OrderID );
    }


    /**
     *
     * 删除订单
     *
     * @param UserID
     * 用户ID
     *
     * @return
     * 返回该用户对应的订单
     */
    public List<OrderModel> getOrderByUserID( int UserID ){

        return null;
    }

    /**
     *
     * 提交订单
     *
     * @param orderID
     * 订单ID
     *
     * @return
     * 返回是否成功
     */
    public String submitteOrder( int orderID ){
        //发送付款成功的消息

        return null;
    }


    /**
     *
     *生成订单ID
     *当前的毫秒级时间疑惑1-10000的随机数  再插入的时候在触发器里与自增数字
     *
     * @return
     * 返回订单ID
     */
    private Long createOrderID(){
        int random = MoneySeverRandom.getRandomNum( 1,10000 );
        long orderTime = System.currentTimeMillis();
        return orderTime^random;
    }

}
