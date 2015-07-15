package com.money.dao.activityDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.memcach.MemCachService;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.OrderModel;
import org.springframework.stereotype.Repository;
import until.GsonUntil;

/**
 * 项目服务
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

@Repository
public class activityDAO extends BaseDao {

    /**
     * 获得项目详细内容
     * @param activityID
     * @return
     */
    public ActivityDetailModel getActivityDetails( int activityID ){

        String UserID = Long.toString( activityID );
        if(MemCachService.KeyIsExists( UserID ) ){

            String activityJson = MemCachService.MemCachgGet( UserID );

            ActivityDetailModel activitymodel = GsonUntil.jsonToJavaClass( activityJson,OrderModel.class );
            return activitymodel;
        }else{
            try{
                ActivityDetailModel activitymodel = (ActivityDetailModel)this.load(OrderModel.class, activityID);
                return activitymodel;
            }catch ( Exception e ){
                return null;
            }
        }
    }


    /**
     * 获得项目动态内容:当前的投资的金额 投资的人数
     * @param activityID
     * @return
     */
    public ActivityDynamicModel getActivityDynamic( int activityID ){

        String UserID = Long.toString( activityID );
        if(MemCachService.KeyIsExists( UserID ) ){

            String activityJson = MemCachService.MemCachgGet( UserID );

            ActivityDynamicModel activitydynamicmodel = GsonUntil.jsonToJavaClass( activityJson,OrderModel.class );
            return activitydynamicmodel;
        }else{
            try{
                ActivityDynamicModel activitydynamicmodel = (ActivityDynamicModel)this.load(OrderModel.class, activityID);
                return activitydynamicmodel;
            }catch ( Exception e ){
                return null;
            }
        }
    }

    /**
     * 插入项目购买人
     * @param activityID
     * @return
     */
    public String InsertUserToBuyList( int activityID,int userID,int Lines ){
        String DBName = Integer.toString( activityID );
        //如果没有查询表则创建
        if( !this.IsModelExist( DBName ) ) {

        }

        //获得当前最新的存储表的名字
        String MaxDBName = DBName + "_1";

        long count = this.getTotalCount( MaxDBName );

        if( count >= Config.MAXDBROWNUM ) {
            //新建一张表 并更新这个总表 然后插入到这张总表里面

           return null;
        }else{
            //插入到这张表中

            return null;
        }
    }
}
