package com.money.dao.PurchaseInAdvanceDAO;

import com.money.Service.order.OrderService;
import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.dao.activityDAO.activityDAO;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.PurchaseInAdvanceModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liumin on 15/7/27.
 */

@Repository
public class PurchaseInAdvanceDAO extends BaseDao {

    @Autowired
    private activityDAO activityDAO;

    /**
     * 插入预购买表
     *
     * @param ActivityID  父项目ID
     * @param UserID
     * @param PurchaseNum
     * @param AdvanceNum
     */
    public void InsertPurchaseInAdvance(String ActivityID, String UserID, int PurchaseNum, int AdvanceNum, int PurchaseType) {
        String DBName = Config.ACTIVITYPURCHASE + ActivityID;

        String sql = "insert into " + DBName +
                " ( UserID,PurchaseInAdvanceNum,CurPurchaseInAdvanceNum,PurchaseNum,PurchaseType ) values ( ?,?,?,?,? )";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, AdvanceNum);
        query.setParameter(2, 0);
        query.setParameter(3, PurchaseNum);
        query.setParameter(4, PurchaseType);
        query.executeUpdate();
    }

    /**
     * 预购成功列表
     *
     * @param ActivityID 父项目ID
     * @param findnum    查找的数量
     * @param page       页数
     */
    public List<PurchaseInAdvanceModel> PurchaseInAdvanceCompelete(String ActivityID, int findnum, int page) {
        String DBNmae = Config.ACTIVITYPURCHASE + ActivityID;
        String sql = "select * from " + DBNmae + " where PurchaseInAdvanceNum>CurPurchaseInAdvanceNum limit ?,?";

        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();

        try {
            SQLQuery query = session.createSQLQuery(sql).addEntity(PurchaseInAdvanceModel.class);

            query.setParameter(0, page);
            query.setParameter(1, findnum);
            List<PurchaseInAdvanceModel> list = query.list();
            t.commit();
            return list;
        } catch (Exception e) {
            t.rollback();
            return null;
        }

    }

    /**
     * 刷新预购项目次数
     *
     * @param UserID
     * @param ActivityID             父项目ID
     * @param curPurchaseActivityNum 当前的次数
     * @return
     */
    public int UpdatePurchaseActivityNum(String UserID, String ActivityID, int curPurchaseActivityNum) {
        String DBNmae = Config.ACTIVITYPURCHASE + ActivityID;
        String sql = "update " + DBNmae + " set CurPurchaseInAdvanceNum=? where UserID=?";

        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        try {
            SQLQuery query = session.createSQLQuery(sql);

            query.setParameter(0, curPurchaseActivityNum);
            query.setParameter(1, UserID);

            query.executeUpdate();
            t.commit();
        } catch (Exception e) {
            t.rollback();
        }


        return 0;
    }

    /**
     * 单词购买项目
     *
     * @param UserID
     * @param ActivityID  父项目ID
     * @param PurchaseNum 当前的次数
     * @return
     */
    public void PurchaseActivity(String ActivityID, String UserID, int PurchaseNum, int PurchaseType) throws Exception {
        //刷新项目票的表 票的所有者
        String DBNmae = Config.ACTIVITYGROUPTICKETNAME + ActivityID;
        String sql = "update " + DBNmae + " set userId=? where userId='0' and PurchaseType=? limit ?";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, PurchaseType);
        query.setParameter(2, PurchaseNum);

        //刷新资金信息
        ActivityDynamicModel activityDynamicModel = activityDAO.getActivityDynamicModelNoTransaction(ActivityID);
        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDynamicModel.getActivityVerifyCompleteModel();

        if (activityDynamicModel == null || activityVerifyCompleteModel == null) {
            return;
        }

        switch (PurchaseType) {
            case Config.PURCHASELOCALTYRANTS:
                int totalPeoples = activityDynamicModel.getActivityTotalLinesPeoples();
                activityDynamicModel.setActivityCurLinesPeoples(totalPeoples);
                int curLines = activityVerifyCompleteModel.getCurFund();
                curLines += totalPeoples;
                activityVerifyCompleteModel.setCurFund(curLines);
                break;
            case Config.PURCHASEPRICKSILK:
                activityDynamicModel.setActivityCurLines(PurchaseNum);
                int curLines1 = activityVerifyCompleteModel.getCurFund();
                curLines1 += PurchaseNum;
                activityVerifyCompleteModel.setCurFund(curLines1);
                break;
        }

        query.executeUpdate();
        session.update(activityDynamicModel);
        session.update(activityVerifyCompleteModel);

    }
}
