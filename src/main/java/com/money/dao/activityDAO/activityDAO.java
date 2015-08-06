package com.money.dao.activityDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.dao.TransactionSessionCallback;
import com.money.memcach.MemCachService;
import com.money.model.*;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;
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
     *
     * @param InstallmentActivityID 分期项目ID
     * @return
     */
    public ActivityDetailModel getActivityDetails(String InstallmentActivityID) {

        if (MemCachService.KeyIsExists(InstallmentActivityID)) {
            String activityJson = MemCachService.MemCachgGet(InstallmentActivityID);
            ActivityDetailModel activityDetailModel = GsonUntil.jsonToJavaClass(activityJson, ActivityDetailModel.class);
            return activityDetailModel;
        } else {
            try {
                ActivityDetailModel activityDetailModel = getActivityDetail(InstallmentActivityID);
                return activityDetailModel;
            } catch (Exception e) {
                return null;
            }
        }
    }


    /**
     * 获得项目动态内容:当前的投资的金额 投资的人数
     *
     * @param InstallmentActivityID
     * @return
     */
    public ActivityDynamicModel getActivityDynamic(String InstallmentActivityID) {

        if (MemCachService.KeyIsExists(InstallmentActivityID)) {
            String activityJson = MemCachService.MemCachgGet(InstallmentActivityID);
            ActivityDynamicModel activitydynamicmodel = GsonUntil.jsonToJavaClass(activityJson, OrderModel.class);
            return activitydynamicmodel;
        } else {
            try {
                ActivityDynamicModel activitydynamicmodel = this.getActivityDynamicModel(InstallmentActivityID);
                return activitydynamicmodel;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 插入项目购买人
     *
     * @param activityID
     * @return
     */
    public String InsertUserToBuyList(int activityID, int userID, int Lines) {
        String DBName = Integer.toString(activityID);
        //如果没有查询表则创建
/*        if( !this.IsModelExist( DBName ) ) {

            this.excuteBySQL( " CREATE TABLE `moneyserver`.`1` (" + "`id` INT NOT NULL," + "`tablename` VARCHAR(45) NULL," + "PRIMARY KEY (`id`));" );
        }*/

        List list = this.getListBySQL("select tablename from activity_1 where id=(select max(id) from activity_1)");


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
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ActivityDetailModel> getActivityListActivity(int page, int pageNum) {
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
        for (ActivityDetailModel detailModel : list) {
            detailModel.getActivityVerifyCompleteModel().getActivityId();
            detailModel.getDynamicModel().getActivityCurLines();
        }
//        ActivityDetailModel activityDynamicModel = (ActivityDetailModel)session.get(ActivityDetailModel.class, "4_0");
//        activityDynamicModel.getDynamicModel().getActivityCurLines();
        t.commit();

        return list;
    }

    /**
     * 根据分期ID获得项目信息
     *
     * @param InstallmentActivityID
     * @return
     */
    public ActivityDetailModel getActivityDetail(final String InstallmentActivityID) {

        final ActivityDetailModel[] activityDetailModels = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                activityDetailModels[0] = (ActivityDetailModel) basedao.getNewSession().createCriteria(ActivityDetailModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("activityStageId", InstallmentActivityID))
                        .uniqueResult();
            }
        });

        return activityDetailModels[0];
    }

    /**
     * 根据过滤ID获得项目信息
     *
     * @param InstallmentActivityID
     * @return
     */
    public ActivityDynamicModel getActivityDynamicModel(final String InstallmentActivityID) {

        final ActivityDynamicModel[] activityDynamicModels = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                activityDynamicModels[0] = (ActivityDynamicModel) basedao.getNewSession().createCriteria(ActivityDynamicModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("activityStageId", InstallmentActivityID))
                        .uniqueResult();
            }
        });

        return activityDynamicModels[0];
    }

    /**
     * 根据过滤ID获得项目信息
     *
     * @param InstallmentActivityID
     * @return
     */
    public ActivityDynamicModel getActivityDynamicModelNoTransaction(final String InstallmentActivityID) {

        final ActivityDynamicModel[] activityDynamicModels = {null};
        activityDynamicModels[0] = (ActivityDynamicModel) this.getNewSession().createCriteria(ActivityDynamicModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("activityStageId", InstallmentActivityID))
                .uniqueResult();
        return activityDynamicModels[0];
    }


    /**
     * 获取项目
     *
     * @param ActivityID
     * @return
     */
    public ActivityVerifyCompleteModel getActivityVerifyCompleteModel(final String ActivityID) {
        final ActivityVerifyCompleteModel[] activityVerifyCompleteModels = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                activityVerifyCompleteModels[0] = (ActivityVerifyCompleteModel) basedao.getNewSession().createCriteria(ActivityVerifyCompleteModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("activityId", ActivityID))
                        .uniqueResult();
            }
        });

        return activityVerifyCompleteModels[0];
    }

    /**
     * 创建票表
     *
     * @param InstallmentActivityID
     */
    public void CreateTicketDB(final String InstallmentActivityID) {

        this.excuteTransactionByCallback(new TransactionSessionCallback() {
            public void callback(Session session) throws Exception {
                String DBName = Config.ACTIVITYGROUPTICKETNAME + InstallmentActivityID;
                String Sql = "CREATE TABLE " + DBName + " ( TickID VARCHAR(45) NOT NULL,UserId VARCHAR(45) NULL DEFAULT 0,PurchaseType INT(2) NOT NULL,PRIMARY KEY (TickID));";
                SQLQuery sqlQuery = session.createSQLQuery(Sql);

                sqlQuery.executeUpdate();
            }
        });
    }

    /**
     * 创建预购买表
     *
     * @param ActivityID
     */
    public void CreatePurchaseInAdvanceDB(final String ActivityID) {
        this.excuteTransactionByCallback(new TransactionSessionCallback() {
            public void callback(Session session) throws Exception {
                String DBName = Config.ACTIVITYPURCHASE + ActivityID;
                String Sql = "CREATE TABLE " + DBName + " ( UserID VARCHAR(45) NOT NULL,PurchaseInAdvanceNum INT(5) NOT NULL, CurPurchaseInAdvanceNum INT(5) NOT NULL,PurchaseNum INT(5) NOT NULL,PurchaseType INT(5) NOT NULL, PRIMARY KEY (UserID));";
                SQLQuery sqlQuery = session.createSQLQuery(Sql);

                sqlQuery.executeUpdate();
            }
        });
    }

    /**
     * 查找同组的分期项目
     *
     * @param ActivityID
     */
    public List<ActivityDetailModel> getActivityDetailByGroupID( String ActivityID,int GroupID ){
        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        List<ActivityDetailModel> list = null;
        try{
            list = session.createCriteria(ActivityDetailModel.class)
                    .add(Restrictions.eq("parentActivityId", ActivityID))
                    .add(Restrictions.eq("groupId", GroupID))
                    .list();
            t.commit();
        } catch ( Exception e ){
            t.rollback();
        }
        return list;
    }

    public ActivityDetailModel getActivityInvestInfo(String activityStageId){
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        ActivityDetailModel detailModel = (ActivityDetailModel)session.get(ActivityDetailModel.class, activityStageId);
        detailModel.getSrEarningModels().size();
        detailModel.getDynamicModel().getActivityTotalLinesPeoples();
        t.commit();

        return detailModel;
    }


}
