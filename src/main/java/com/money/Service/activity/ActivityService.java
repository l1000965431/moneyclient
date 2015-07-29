package com.money.Service.activity;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.order.OrderService;
import com.money.Service.ServiceFactory;
import com.google.gson.reflect.TypeToken;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 项目服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Service("ActivityService")
public class ActivityService extends ServiceBase implements ServiceInterface {

    @Autowired
    private activityDAO activityDao;

    @Autowired
    private UserService userService;

    public ActivityDetailModel getActivityDetails( int ActivityID ){
        ActivityDetailModel activitymodel = activityDao.getActivityDetails(ActivityID);
        return activitymodel;
    }

    public ActivityDynamicModel getActivityDynamic( int ActivityID ){
        ActivityDynamicModel activityDynamicModel = activityDao.getActivityDynamic(ActivityID);
        return activityDynamicModel;
    }

    @SuppressWarnings("unchecked")
    public List<ActivityDynamicModel> getAllActivityDetail(){
        return activityDao.getActivityListActivity(0, 1000);
    }

    public List<ActivityDetailModel> getOrderDetailsList( int minpage,int maxpage ){
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

    public int ActivityBuy( int activityID,int activitylines,int activitygroupId,String userID,String token ){

        if( userService.tokenLand( userID,token ) == 0 ){
            return ServerReturnValue.USERNOTLAND;
        }

        if( userService.IsPerfectInfo( userID ) ){
            return ServerReturnValue.USERNOTPERFECTINFO;
        }

        if( !IsActivityIDLegal( activityID ) || !IsActivityExist(activityID) ){
            return ServerReturnValue.ACTIVITYIDERROR;
        }

        if( getActivityState( activityID ) != ActivityDynamicModel.ONLINE_ACTIVITY_START){
            return ServerReturnValue.ACTIVITYSTATEERROR;
        }

        OrderService orderService = ServiceFactory.getService("OrderService");
        if( orderService.createOrder(userID, "2", activitylines, activitygroupId) == Config.SERVICE_SUCCESS ){
            //调用付款接口 并在付款接口中发送订单和项目修改消息 在付款完成接口中 调用订单提交



            return ServerReturnValue.SERVERRETURNCOMPELETE;
        }else{
            return ServerReturnValue.SERVERRETURNERROR;
        }
    }


    public int getActivityState( int activityID ){
        ActivityDynamicModel activityModel = activityDao.getActivityDynamic(activityID);

        return activityModel.getActivityState();
    }

    /**
     * 项目是否存在
     * @param activityID 项目ID
     * @return
     */
    public boolean IsActivityExist( int activityID ){

        ActivityDetailModel activityModel = activityDao.getActivityDetails(activityID);

        if( activityModel == null ){
            return false;
        }

        return true;
    }

    /**
     * 项目ID是否合法
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
            SetActivityCurLines( activitydynamicmodel,addlines );
            activityDao.update( activitydynamicmodel );
            return Config.SERVICE_SUCCESS;
        }catch (Exception e){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 设置项目的当前金额 需要手动提交
     * @param addlines   投资金额
     * @return
     */
    public void SetActivityCurLines( ActivityDynamicModel activitydynamicmodel,int addlines ){
        int activitycurlines = activitydynamicmodel.getActivityState();
        activitydynamicmodel.setActivityCurLines(activitycurlines + addlines);
    }


    /**
     * 检测项目是否完成并设置项目完成
     * @param activityID 项目ID
     * @return
     */
    private boolean IsActivityComplete( int activityID ){
        ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
        return IsActivityComplete( activitydynamicmodel );
    }


    /**
     * 检测项目是否完成并设置项目完成
     * @return
     */
    private boolean IsActivityComplete( ActivityDynamicModel activitydynamicmodel ){
        int curactivitylines = activitydynamicmodel.getActivityCurLines();
        int totalactivitylines = activitydynamicmodel.getActivityTotalAmount();

        if( curactivitylines >= totalactivitylines ){
            activitydynamicmodel.setActivityState(ActivityDynamicModel.ONLINE_ACTIVITY_COMPLETE);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 取消项目
     * @param activityID
     * @return
     */
    public String CanelActivity( final int activityID ){
        if( activityDao.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {

                ActivityDetailModel activityDetailModel = activityDao.getActivityDetails( activityID );
                activityDetailModel.setStatus( ActivityDetailModel.ONLINE_ACTIVITY_FAILED);
                activityDao.update( activityDetailModel );

                ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
                activityDao.delete(activitydynamicmodel );
            }
        }) == Config.SERVICE_SUCCESS ){
            return Config.SERVICE_SUCCESS;
        }else{
            return Config.SERVICE_FAILED;
        }
    }


    /**
     * 获得金额对应的当前人数
     * @param activityID
     * @param Lines
     * @return
     */
    public int GetActivityLinesCurPeoples( int activityID,int Lines ){
        ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic( activityID );
        return  GetActivityLinesCurPeoples( activitydynamicmodel,Lines );
    }

    /**
     * 获得金额对应的当前人数
     * @param
     * @param Lines
     * @return
     */
    public int GetActivityLinesCurPeoples( ActivityDynamicModel activitydynamicmodel,int Lines ){
        try{
            int activitycurlines = activitydynamicmodel.getActivityCurLinesPeoples();
            Map<String,Integer> mappeoples = GsonUntil.jsonToJavaClass( "",new TypeToken<Map<String,Integer>>(){}.getType() );
            int peoples = mappeoples.get( Integer.toString( Lines ) );
            return peoples;
        }catch ( Exception e ){
            return Config.RETURNERROR;
        }
    }

    /**
     * 获得对应金额的总人数
     * @param activityID 项目ID
     * @param Lines      金额
     * @return
     */
    public int GetActivityLinesTotalPeoples( int activityID,int Lines ){
        ActivityDetailModel activityDetailModel = activityDao.getActivityDetails( activityID );
        return GetActivityLinesTotalPeoples( activityDetailModel,Lines );
    }

    /**
     * 获得对应金额的总人数
     * @param
     * @param Lines      金额
     * @return
     */
    public int GetActivityLinesTotalPeoples( ActivityDetailModel activityDetailModel,int Lines ){
        try{
            String activitycurlines = activityDetailModel.getActivityLinesPeoples();
            Map<String,Integer> mappeoples = GsonUntil.jsonToJavaClass( activitycurlines,new TypeToken<Map<String,Integer>>(){}.getType() );
            int peoples = mappeoples.get( Integer.toString( Lines ) );
            return peoples;
        }catch ( Exception e ){
            return Config.RETURNERROR;
        }
    }

    /**
     * 检测当前金额的人数是否已满
     * @param activityID   项目ID
     * @param Lines        金额
     * @return
     */
    public boolean IsActivityLinesPeoplesFull( int activityID,int Lines ){
        ActivityDetailModel activityDetailModel = getActivityDetails(activityID);
        ActivityDynamicModel activityDynamicModel = getActivityDynamic(activityID);
        return IsActivityLinesPeoplesFull( activityDetailModel,activityDynamicModel,Lines );

    }


    /**
     * 检测当前金额的人数是否已满
     * @param Lines        金额
     * @return
     */
    public boolean IsActivityLinesPeoplesFull( ActivityDetailModel activityDetailModel,ActivityDynamicModel activitydynamicmodel,int Lines ){
        int curLinesPeoples = GetActivityLinesCurPeoples( activitydynamicmodel,Lines );
        int totalLinesPeoples = GetActivityLinesTotalPeoples( activityDetailModel,Lines );

        if( curLinesPeoples != Config.RETURNERROR && totalLinesPeoples != Config.RETURNERROR && curLinesPeoples >= totalLinesPeoples ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 增加当前金额
     * @param activityID  项目ID
     * @param addLines    增加的金额
     * @return
     */
    public String AddActivityLines( int activityID, int addLines ){
        try{
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
            AddActivityLines( activitydynamicmodel,addLines );
            activityDao.update(activitydynamicmodel);
            return Config.SERVICE_SUCCESS;
        }catch ( Exception e ){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 增加当前金额
     * @param addLines    增加的金额
     * @return
     */
    public String AddActivityLines( ActivityDynamicModel activitydynamicmodel, int addLines ){
        try{
            int curLines = activitydynamicmodel.getActivityCurLines();
            curLines += addLines;
            activitydynamicmodel.setActivityCurLines(curLines);
            return Config.SERVICE_SUCCESS;
        }catch ( Exception e ){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 设置项目的当前金额对用的人数
     * @param activityID 项目ID
     * @param addlines   投资金额
     * @return
     */
    public String AddActivityCurLinesPeoples( int activityID,int addlines ){
        try {
            ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic(activityID);
            AddActivityCurLinesPeoples( activitydynamicmodel,addlines );
            activityDao.update(activitydynamicmodel);
            return Config.SERVICE_SUCCESS;
        }catch (Exception e){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 设置项目的当前金额对用的人数
     * @param addlines   投资金额
     * @return
     */
    public String AddActivityCurLinesPeoples( ActivityDynamicModel activitydynamicmodel,int addlines ){
        try {
            int activitycurlines = activitydynamicmodel.getActivityCurLinesPeoples();

            Map<String,Integer> mappeoples = GsonUntil.jsonToJavaClass( "",new TypeToken<Map<String,Integer>>(){}.getType() );

            int linespeople = mappeoples.get( Integer.toString( addlines ) );
            linespeople += 1;
            mappeoples.put( Integer.toString( addlines ),linespeople );

            String newlinespeoples = GsonUntil.JavaClassToJson(mappeoples);
            activitydynamicmodel.setActivityCurLinesPeoples(0);
            return Config.SERVICE_SUCCESS;
        }catch (Exception e){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 设置活动完成
     * @param activityID
     * @return
     */
    public String SetActivityCompelete( int activityID ){
        ActivityDynamicModel activitydynamicmodel = activityDao.getActivityDynamic( activityID );
        ActivityDetailModel activityDetailModel = activityDao.getActivityDetails( activityID );
        return SetActivityCompelete( activityDetailModel,activitydynamicmodel );
    }

    /**
     * 设置活动完成
     * @return
     */
    public String SetActivityCompelete( ActivityDetailModel activityDetailModel,ActivityDynamicModel activitydynamicmodel ){
        try{
            int curactivitylines = activitydynamicmodel.getActivityCurLines();
            int totalactivitylines = activitydynamicmodel.getActivityTotalAmount();

            if( curactivitylines >= totalactivitylines ){
                activityDetailModel.setStatus( ActivityDetailModel.ONLINE_ACTIVITY_COMPLETE);
            }
            return Config.SERVICE_SUCCESS;
        }catch ( Exception e ){
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 插入购买者列表
     * @param ActivityID
     * @param UserID
     * @param Lines
     * @return
     */
    public String InsertBuyUser( int ActivityID,int UserID,int Lines ){
        activityDao.InsertUserToBuyList( ActivityID,UserID,Lines );
        return null;
    }



    /**
     * 设置项目组当前的小R金额人数
     * @param
     * @return
     */
    public String SetActivityGroupLinesPeoples( ActivityGroupModel activityGroupModel,int Lines ) {
        try {
            String activitycurlines = "";//activityGroupModel.getSrTickets();

            Map<String, Integer> mappeoples = GsonUntil.jsonToJavaClass(activitycurlines, new TypeToken<Map<String, Integer>>() {
            }.getType());

            int linespeople = mappeoples.get(Integer.toString(Lines));
            linespeople += 1;
            mappeoples.put(Integer.toString(Lines), linespeople);

            String newlinespeoples = GsonUntil.JavaClassToJson(mappeoples);
            //activityGroupModel.setSrTickets(newlinespeoples);
            return Config.SERVICE_SUCCESS;
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
    }

    /**
     * 项目组是否完成
     * @param activityGroupModel
     * @return
     */
    public boolean IsActivityGroupCompelete( ActivityGroupModel activityGroupModel ){




        return false;
    }

    /**
     * 获得项目组小R当前投资的人数
     * @param activityGroupModel
     * @return
     */

    public int GetActivityGroupPeople( ActivityGroupModel activityGroupModel ){

        int curpeoples = 0;
        String activitycurlines = "";//activityGroupModel.getSrTickets();

        Map<String, Integer> mappeoples = GsonUntil.jsonToJavaClass(activitycurlines, new TypeToken<Map<String, Integer>>() {
        }.getType());

        Iterator it=mappeoples.entrySet().iterator();

        while( it.hasNext() ){
            Map.Entry entry = (Map.Entry)it.next();
            int value= Integer.valueOf( entry.getValue().toString());
            curpeoples += value;
        }
        return curpeoples;
    }


    /**
     * 获得项目大R当前投资的人数
     * @param activityDetailModel
     * @return
     */

    public int GetActivityPeople( ActivityDetailModel activityDetailModel ){

        int curpeoples = 0;
        String activitycurlines = activityDetailModel.getActivityLinesPeoples();

        Map<String, Integer> mappeoples = GsonUntil.jsonToJavaClass(activitycurlines, new TypeToken<Map<String, Integer>>() {
        }.getType());

        Iterator it=mappeoples.entrySet().iterator();

        while( it.hasNext() ){
            Map.Entry entry = (Map.Entry)it.next();
            int value= Integer.valueOf( entry.getValue().toString());
            curpeoples += value;
        }
        return curpeoples;
    }

    /**
     * 分页查找项目详情
     * @param pagenumber  每页显示多少条信息
     * @param page        显示第几页
     * @return
     */
    public List GetActivityDetailsWithPage( int pagenumber,int page ){

        return null;
    }

    /**
     * 分页查找项目动态信息
     * @param pagenumber  每页显示多少条信息
     * @param page        显示第几页
     * @return
     */
    public List GetActivityDynamicWithPage( int pagenumber,int page ){

        return null;
    }


    /**
     * 用户已经收益的项目
     * @param UserID
     * @return
     */
     public List GetActivityHasEarnings( String UserID ){

       return null;
    }


    /**
     * 用户已经收益的项目
     * @param UserID
     * @return
     */
    public List GetActivityHasInvestment( String UserID ){

        return null;
    }

    /**
     * 项目分期
     */
    public String ActivityInstallment( List<ActivityDetailModel> newInstallmentList ){

        List<ActivityDetailModel> OldInstallmentList = null;

        List<ActivityDetailModel> DeleteInstallmentList = null;

        for( ActivityDetailModel newIt:newInstallmentList ){
            //项目状态尚未开始 可以修改分期状态
            if( newIt.getStatus() == Config.NOINSTALLMENTACTIVITYSTART ){
                activityDao.saveOrupdate( newIt );
            }
        }

        for( ActivityDetailModel It: OldInstallmentList ){
            for( ActivityDetailModel It1: newInstallmentList ){
                if( It != It1 && It1.getStatus() == Config.NOINSTALLMENTACTIVITYSTART ){
                    DeleteInstallmentList.add( It );
                    break;
                }
            }
        }

        //批量删除分期项目
        // DeleteInstallmentList


        return null;
    }


    /**
     * 后台修改项目的状态
     */
    public String SetActivityState( int State,String ActivityID ){

        ActivityVerifyModel activityVerifyModel = (ActivityVerifyModel)activityDao.load(ActivityVerifyModel.class, ActivityID);

        activityVerifyModel.setAuditorStatus( State );

        activityDao.update( activityVerifyModel );

        return null;
    }

    /**
     * 获得项目剩余的期数
     * @param ActivityID 父项目ID
     * @return
     */
    public int getActivityRemainingInstallment( String ActivityID ) {

        return 0;
    }

    /**
     * 获得分期项目剩余的票数
     * @param InstallmentActivityID 分期项目ID
     * @return
     */

    public int getActivityRemainingTickets( String InstallmentActivityID ) {

        return 0;
    }
}
