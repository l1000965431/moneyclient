package com.money.Service.activity;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.order.OrderService;
import com.money.Service.ServiceFactory;
import com.google.gson.reflect.TypeToken;
import com.money.config.Config;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.List;
import java.util.Map;

/**
 * 项目服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Service
public class ActivityService extends ServiceBase implements ServiceInterface {

    @Autowired
    private activityDAO activityDao;

    public ActivityModel getOrderDetails( int ActivityID ){
        try{
            ActivityModel activitymodel = activityDao.getActivityDetails( ActivityID );
            return activitymodel;
        }catch ( Exception e ){
            return null;
        }
    }

    public List<ActivityModel> getOrderDetailsList( int minpage,int maxpage ){
        try{
            return null;
        }catch ( Exception e ){
            return null;
        }
    }


    /**
     * 项目投资
     * @param activityID  项目ID
     * @param activitylines 投资金额
     * @return  是否成功
     */

    public String ActivityBuy( int activityID,int userID,int activitylines,int activitygroupId ){
        if( !IsActivityIDLegal( activityID ) || !IsActivityExist(activityID) ){
            return Config.SERVICE_FAILED;
        }

        if( getActivityState( activityID ) != ActivityDynamicModel.ONLINEACTIVITY_START ){
            return Config.SERVICE_FAILED;
        }

        OrderService orderService = ServiceFactory.getService( "OrderService" );
        if( orderService.createOrder(userID, activityID, activitylines, activitygroupId) == Config.SERVICE_SUCCESS ){
            //调用付款接口 并在付款接口中发送订单和项目修改消息 在付款完成接口中 调用订单提交



            return Config.SERVICE_SUCCESS;
        }else{
            return Config.SERVICE_FAILED;
        }
    }


    public int getActivityState( int activityID ){
        ActivityDynamicModel activityModel = activityDao.getActivityDynamic(activityID);

        return activityModel.getActivitystate();
    }

    /**
     * 订单是否存在
     * @param activityID 订单ID
     * @return
     */
    public boolean IsActivityExist( int activityID ){

        ActivityModel activityModel = activityDao.getActivityDetails( activityID );

        if( activityModel == null ){
            return false;
        }

        return true;
    }

    /**
     * 订单ID是否合法
     * @param activityID
     * @return
     */
    public boolean IsActivityIDLegal( int activityID ){

        return true;
    }

    /**
     * 设置项目的当前金额
     * @param activityID 项目ID
     * @param addlines   投资金额
     * @return
     */
    public String SetActivityCurLines( int activityID,int addlines ){
        try {
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
            int activitycurlines = activitydynamicmodel.getActivitystate();
            activitydynamicmodel.setActivitycurlines(activitycurlines + addlines);
            return Config.SERVICE_SUCCESS;
        }catch (Exception e){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 设置项目的当前金额
     * @param activityID 项目ID
     * @param addlines   投资金额
     * @return
     */
    public String SetActivityCurLinesPeoples( int activityID,int addlines ){
        try {
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
            String activitycurlines = activitydynamicmodel.getActivitycurlinespeoples();

            Map<String,Integer> mappeoples = GsonUntil.jsonToJavaClass( activitycurlines,new TypeToken<Map<String,Integer>>(){}.getType() );

            int linespeople = mappeoples.get( Integer.toString( addlines ) );
            linespeople += 1;
            mappeoples.put( Integer.toString( addlines ),linespeople );

            String newlinespeoples = GsonUntil.JavaClassToJson( mappeoples );
            activitydynamicmodel.setActivitycurlinespeoples( newlinespeoples );

            return Config.SERVICE_SUCCESS;
        }catch (Exception e){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 检测项目是否完成并设置项目完成
     * @param activityID 项目ID
     * @return
     */
    private boolean IsActivityComplete( int activityID ){
        ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic( activityID );
        int curactivitylines = activitydynamicmodel.getActivitycurlines();
        int totalactivitylines = activitydynamicmodel.getActivitytotalamount();

        if( curactivitylines >= totalactivitylines ){
            activitydynamicmodel.setActivitystate( ActivityDynamicModel.ONLINEACTIVITY_COMPLETE );
            activityDao.save( activitydynamicmodel );
            return true;
        }else{
            return false;
        }
    }


}
