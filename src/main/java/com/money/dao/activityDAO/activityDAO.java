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
        ActivityDetailModel activityDetailModel;
        if (MemCachService.KeyIsExists(InstallmentActivityID)) {
            String activityJson = MemCachService.MemCachgGet(InstallmentActivityID);
            activityDetailModel = GsonUntil.jsonToJavaClass(activityJson, ActivityDetailModel.class);
            return activityDetailModel;
        } else {
            try {
                activityDetailModel = getActivityDetail(InstallmentActivityID);
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
        ActivityDynamicModel activitydynamicmodel;
        if (MemCachService.KeyIsExists(InstallmentActivityID)) {
            String activityJson = MemCachService.MemCachgGet(InstallmentActivityID);
            activitydynamicmodel = GsonUntil.jsonToJavaClass(activityJson, OrderModel.class);
            return activitydynamicmodel;
        } else {
            try {
                activitydynamicmodel = this.getActivityDynamicModel(InstallmentActivityID);
                return activitydynamicmodel;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 获得项目动态内容:当前的投资的金额 投资的人数
     *
     * @param ActivityID
     * @return
     */
    public ActivityDynamicModel getActivityCompelete(String ActivityID) {
        ActivityDynamicModel activitydynamicmodel;
        try {
            activitydynamicmodel = this.getActivityDynamicModel(ActivityID);
            return activitydynamicmodel;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 插入项目购买人
     *
     * @param activityID
     * @return
     *//*
    public String InsertUserToBuyList(int activityID, int userID, int Lines) {
        String DBName = Integer.toString(activityID);
        //如果没有查询表则创建
*//*        if( !this.IsModelExist( DBName ) ) {

            this.excuteBySQL( " CREATE TABLE `moneyserver`.`1` (" + "`id` INT NOT NULL," + "`tablename` VARCHAR(45) NULL," + "PRIMARY KEY (`id`));" );
        }*//*

        List list = this.getListBySQL("select tablename from activity_1 where id=(select max(id) from activity_1)");


*//*        //获得当前最新的存储表的名字
        String MaxDBName = DBName + "_1";

        long count = this.getTotalCount( MaxDBName );

        if( count >= Config.MAXDBROWNUM ) {
            //新建一张表 并更新这个总表 然后插入到这张总表里面

           return null;
        }else{
            //插入到这张表中

            return null;
        }*//*
        return null;
    }*/

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

    public List<ActivityDetailModel> getActivityListActivityTest(int page, int pageNum) {
        Session session = getNewSession();
        Transaction t = session.beginTransaction();
        List<ActivityDetailModel> list = session.createCriteria(ActivityDetailModel.class)
                .add(Restrictions.eq("status", ActivityDetailModel.ONLINE_ACTIVITY_TEST))
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
     * 根据分期ID获得项目信息
     *
     * @param InstallmentActivityID
     * @return
     */
    public ActivityDetailModel getActivityDetaillNoTransaction(String InstallmentActivityID) {
        final ActivityDetailModel activityDetailModels;
        activityDetailModels = (ActivityDetailModel) this.getNewSession().createCriteria(ActivityDetailModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("activityStageId", InstallmentActivityID))
                .uniqueResult();
        return activityDetailModels;
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

        final ActivityDynamicModel activityDynamicModels;
        activityDynamicModels = (ActivityDynamicModel) this.getNewSession().createCriteria(ActivityDynamicModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("activityStageId", InstallmentActivityID))
                .uniqueResult();
        return activityDynamicModels;
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

    public ActivityVerifyCompleteModel getActivityVerifyCompleteModelNoTransaction(final String ActivityID) {
        final ActivityVerifyCompleteModel[] activityVerifyCompleteModels = {null};

        activityVerifyCompleteModels[0] = (ActivityVerifyCompleteModel) this.getNewSession().createCriteria(ActivityVerifyCompleteModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("activityId", ActivityID))
                .uniqueResult();
        return activityVerifyCompleteModels[0];
    }

    /**
     * 创建票表 (修改为存储过程创建)
     *
     * @param InstallmentActivityID
     */
    public void CreateTicketDB(final String InstallmentActivityID,int TotalLinePeoples, int TotalLines) {
        Session session = this.getNewSession();
        String DBName = Config.ACTIVITYGROUPTICKETNAME + InstallmentActivityID;
        String Sql = "CREATE TABLE " + DBName + " ( TickID VARCHAR(45) NOT NULL,UserId VARCHAR(45) NULL DEFAULT 0,PurchaseType INT(2) NOT NULL,PRIMARY KEY (TickID));";
        //String Sql = "{call CreateTicketDB(?,?,?)}";
        SQLQuery sqlQuery = session.createSQLQuery(Sql);
/*        sqlQuery.setParameter( 0,1 );
        sqlQuery.setParameter( 1,TotalLines );
        sqlQuery.setParameter( 2,DBName );*/
        sqlQuery.executeUpdate();
    }

    /**
     * 创建预购买表
     *
     * @param ActivityID
     */
    public void CreatePurchaseInAdvanceDB(final String ActivityID) {

        Session session = getNewSession();
        String DBName = Config.ACTIVITYPURCHASE + ActivityID;
        String Sql = "CREATE TABLE " + DBName + " ( UserID VARCHAR(45) NOT NULL,PurchaseInAdvanceNum INT(5) NOT NULL, " +
                "CurPurchaseInAdvanceNum INT(5) NOT NULL,PurchaseNum INT(5) NOT NULL," +
                "PurchaseType INT(5) NOT NULL, PurchaseInAdvanceNumID VARCHAR(255) NOT NULL,PurchaseTime DATETIME NOT NULL, PRIMARY KEY (PurchaseInAdvanceNumID));";
        SQLQuery sqlQuery = session.createSQLQuery(Sql);

        sqlQuery.executeUpdate();

    }

    /**
     * 查找同组的分期项目
     *
     * @param ActivityID
     */
    public List<ActivityDetailModel> getActivityDetailByGroupID(String ActivityID, int GroupID) {
        Session session = this.getNewSession();
        List<ActivityDetailModel> list;
        String sql = "select * from activitydetails where activityVerifyCompleteModel_activityId=?0 and groupId=?1;";

        SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(ActivityDetailModel.class);
        sqlQuery.setParameter(0, ActivityID);
        sqlQuery.setParameter(1, GroupID);
        list = sqlQuery.list();

        return list;
    }

    public ActivityDetailModel getActivityInvestInfo(String activityStageId) {
        Session session = getNewSession();
        ActivityDetailModel detailModel = (ActivityDetailModel) session.get(ActivityDetailModel.class, activityStageId);
        detailModel.getSrEarningModels().size();
        detailModel.getDynamicModel().getActivityTotalLinesPeoples();
        return detailModel;
    }

    public void changeActivityStatus(String activityId, int status){
        ActivityVerifyCompleteModel completeModel = (ActivityVerifyCompleteModel)load(ActivityVerifyCompleteModel.class, activityId);
        completeModel.setStatus(status);
        update(completeModel);
    }

}
