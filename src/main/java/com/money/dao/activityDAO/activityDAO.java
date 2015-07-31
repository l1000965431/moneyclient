package com.money.dao.activityDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.memcach.MemCachService;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.OrderModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import until.GsonUntil;

import java.util.List;

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
                ActivityDetailModel activitymodel = (ActivityDetailModel)this.load(ActivityDetailModel.class, (long)activityID);
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
                ActivityDynamicModel activitydynamicmodel = (ActivityDynamicModel)this.load(ActivityDynamicModel.class, activityID);
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
/*        if( !this.IsModelExist( DBName ) ) {

            this.excuteBySQL( " CREATE TABLE `moneyserver`.`1` (" + "`id` INT NOT NULL," + "`tablename` VARCHAR(45) NULL," + "PRIMARY KEY (`id`));" );
        }*/

        List list = this.getListBySQL( "select tablename from activity_1 where id=(select max(id) from activity_1)" );


/*        //获得当前最新的存储表的名字
        String MaxDBName = DBName + "_1";

        long count = this.getTotalCount( MaxDBName );

        if( count >= Config.MAXDBROWNUM ) {
            //新建一张表 并更新这个总表 然后插入到这张总表里面

           return null;
        }else{
            //插入到这张表中

            return null;
        }*/
        return null;
    }

    /**
     * 得到app需要显示的项目列表
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ActivityDetailModel> getActivityListActivity(int page, int pageNum){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        List<ActivityDetailModel> list = session.createCriteria(ActivityDetailModel.class)
                .add(Restrictions.eq("status", 1))
                .setFirstResult(page * pageNum)
                .setMaxResults(pageNum)
                .list();

//        String hql = "from " + ActivityDetailModel.class.getName();
//        Session session = getNewSession();
//        List<ActivityDetailModel> list =  session.createQuery(hql)
//                .setFirstResult(pageNum * page)
//                .setMaxResults(pageNum)
//                .list();
        for(ActivityDetailModel detailModel : list){
            detailModel.getActivityVerifyCompleteModel().getActivityId();
            detailModel.getDynamicModel().getActivityCurLines();
        }
//        ActivityDetailModel activityDynamicModel = (ActivityDetailModel)session.get(ActivityDetailModel.class, "4_0");
//        activityDynamicModel.getDynamicModel().getActivityCurLines();
//        t.commit();

        return list;
    }
}
