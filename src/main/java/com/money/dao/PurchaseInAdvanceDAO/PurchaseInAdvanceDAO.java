package com.money.dao.PurchaseInAdvanceDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.model.PurchaseInAdvanceModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liumin on 15/7/27.
 */

@Repository
public class PurchaseInAdvanceDAO extends BaseDao {


    /**
     * 插入预购买表
     *
     * @param ActivityID  父项目ID
     * @param UserID
     * @param PurchaseNum
     * @param AdvanceNum
     */
    public void InsertPurchaseInAdvance(String ActivityID, String UserID, int PurchaseNum, int AdvanceNum,int PurchaseType ) {
        String DBNmae = Config.ACTIVITYPURCHASE + ActivityID;

        String sql = "insert into " + DBNmae +
                " ( ActivityID,UserID,PurchaseInAdvanceNum,CurPurchaseInAdvanceNum,PurchaseNum,PurchaseType ) values ( ?,?,?,?,?,? )";

        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, ActivityID);
        query.setParameter(1, UserID);
        query.setParameter(2, PurchaseNum);
        query.setParameter(3, 0);
        query.setParameter(4, AdvanceNum);
        query.setParameter(5, PurchaseType);

        query.executeUpdate();
        t.commit();
    }

    /**
     * 预购成功列表
     *
     * @param ActivityID 父项目ID
     * @param findnum    查找的数量
     * @param page       页数
     */
    public List<PurchaseInAdvanceModel> PurchaseInAdvanceCompelete( String ActivityID, int findnum, int page ) {
        String DBNmae = "purchaseinadvance";  //Config.ACTIVITYPURCHASE+ActivityID;
        String sql = "select * from " + DBNmae + " where PurchaseInAdvanceNum>CurPurchaseInAdvanceNum limit ?,?";

        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(sql).addEntity(PurchaseInAdvanceModel.class);

        query.setParameter(0, page);
        query.setParameter(1, findnum);
        List<PurchaseInAdvanceModel> list = query.list();
        t.commit();
        return list;
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
        String DBNmae = "purchaseinadvance";//Config.ACTIVITYPURCHASE+ActivityID;
        String sql = "update " + DBNmae + " set CurPurchaseInAdvanceNum=? where UserID=?";

        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(sql);

        query.setParameter(0, curPurchaseActivityNum);
        query.setParameter(1, UserID);

        query.executeUpdate();
        t.commit();

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
    public void PurchaseActivity(String ActivityID, String UserID, int PurchaseNum,int PurchaseType ) {
        //刷新项目票的表 票的所有者
        String DBNmae = "ticket";//Config.ACTIVITYGROUPTICKETNAME + ActivityID;
        String sql = "update " + DBNmae + " set userId=? where userId=0 and PurchaseType=? limit ?";
        Session session = this.getNewSession();
        Transaction t = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, PurchaseNum);
        query.setParameter(2, PurchaseType);
        try {
            query.executeUpdate();
            t.commit();
        } catch (Exception e) {
            t.rollback();
        }
    }
}
