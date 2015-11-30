package com.money.Service.order;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.activityDAO.activityDAO;
import com.money.dao.orderDAO.OrderDAO;
import com.money.model.ActivityDetailModel;
import com.money.model.OrderModel;
import org.hibernate.StaleObjectStateException;
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
    private OrderDAO orderDAO;

    @Autowired
    private activityDAO activityDAO;

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
    public String createOrder( String userID,String activityID,int lines,int PurchaseNum,int AdvanceNum,int purchasType,String OrderID,int StartOrderAndvance ){
        OrderModel orderModel = new OrderModel();
        ActivityDetailModel activityDetailModel =  activityDAO.getActivityDetaillNoTransaction(activityID);
        orderModel.setActivityDetailModel(activityDetailModel);
        orderModel.setOrderLines(lines);
        orderModel.setUserId(userID);
        orderModel.setOrderId(OrderID);
        orderModel.setPurchaseNum( PurchaseNum );
        orderModel.setAdvanceNum( AdvanceNum );
        orderModel.setPurchaseType( purchasType );
        orderModel.setOrderStartAdvance( StartOrderAndvance );
        try {
            orderModel.setOrderDate(MoneyServerDate.getDateCurDate());
            orderDAO.saveNoTransactionTest(orderModel);
        } catch (ParseException e) {
            return Config.SERVICE_FAILED;
        }catch( StaleObjectStateException e ){
            return Config.SERVICE_FAILED;
        }

        //orderDAO.saveNoTransaction( orderModel );


        return Config.SERVICE_SUCCESS;
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

        //orderModel.setOrderState(OrderModel.ORDER_STATE_SUBMITTECANEL);
        orderDAO.update(orderModel);

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
            orderDAO.delete( orderModel );
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
        return (OrderModel)orderDAO.load( OrderModel.class,OrderID );
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
        OrderModel orderModel = getOrderByOrderID( orderID );

        if( orderModel == null ){
            return  Config.SERVICE_FAILED;
        }

        //orderModel.setOrderState(OrderModel.ORDER_STATE_SUBMITTSUCCESS);
        orderDAO.update( orderModel );
        return Config.SERVICE_FAILED;
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


    /**
     * 根据用户ID 获得订单序列
     * @param UserID
     * @param
     * @return
     */
    public List getOrderByUserID( String UserID,int page,int findNum ){
          return orderDAO.getOrderByUserID( UserID,page,findNum );
    }

    public int TestGentou(){
        return orderDAO.TestGenTou();
    }

    public int TestLingTou(){
         return orderDAO.TestLingTou();
    }


}
