package com.money.dao.PurchaseInAdvanceDAO;

import com.money.Service.order.OrderService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.BaseDao;
import com.money.dao.activityDAO.activityDAO;
import com.money.dao.userDAO.UserDAO;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import com.money.model.PurchaseInAdvanceModel;
import javassist.convert.Transformer;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import until.MoneyServerDate;

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
    public void InsertPurchaseInAdvance(String ActivityID, String UserID, int PurchaseNum, int AdvanceNum, int PurchaseType,String OrderID ) {
        String DBName = Config.ACTIVITYPURCHASE + ActivityID;

        String sql = "insert into " + DBName +
                " ( UserID,PurchaseInAdvanceNum,CurPurchaseInAdvanceNum,PurchaseNum,PurchaseType,PurchaseInAdvanceNumID,PurchaseTime ) values ( ?,?,?,?,?,?,? )";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, AdvanceNum);
        query.setParameter(2, 0);
        query.setParameter(3, PurchaseNum);
        query.setParameter(4, PurchaseType);
        query.setParameter(5, OrderID);
        query.setParameter(6, MoneyServerDate.getStringCurDate());
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
        String sql = "select UserID,PurchaseInAdvanceNumID," +
                "PurchaseInAdvanceNum,CurPurchaseInAdvanceNum,PurchaseNum,PurchaseType from " +
                DBNmae + " where PurchaseInAdvanceNum>CurPurchaseInAdvanceNum order by PurchaseTime ASC limit ?,?";
        Session session = this.getNewSession();
        try {
            Query query = session.createSQLQuery(sql).
                    setResultTransformer(Transformers.aliasToBean(PurchaseInAdvanceModel.class));

            query.setParameter(0, page);
            query.setParameter(1, findnum);
            List<PurchaseInAdvanceModel> list = query.list();
            return list;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 刷新预购项目次数
     *
     * @param UserID
     * @param ActivityID             父项目ID
     * @return
     */
    public int UpdatePurchaseActivityNum(String Id, String UserID, String ActivityID) throws Exception {
        String DBNmae = Config.ACTIVITYPURCHASE + ActivityID;
        String sql = "update " + DBNmae + " set CurPurchaseInAdvanceNum=CurPurchaseInAdvanceNum+1" +
                " where UserID=? and PurchaseInAdvanceNumID=? and CurPurchaseInAdvanceNum+1 <= PurchaseInAdvanceNum ";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, Id);
        return query.executeUpdate();
    }

    /**
     * 单词购买项目
     *
     * @param UserID
     * @param InstallmentActivityID  父项目ID
     * @param PurchaseNum 当前的次数
     * @return
     */
    public int PurchaseActivity(String InstallmentActivityID, String UserID, int PurchaseNum, int PurchaseType,int Lines ) throws Exception {
        //刷新项目票的表 票的所有者
        String DBNmae = Config.ACTIVITYGROUPTICKETNAME + InstallmentActivityID;

        Session session = this.getNewSession();
        String sqlCount = "select count(PurchaseType) from " + DBNmae + " where UserId='0' and PurchaseType=?;";

        SQLQuery queryCount = session.createSQLQuery(sqlCount);
        queryCount.setParameter(0, PurchaseType);
        int count = Integer.parseInt(queryCount.uniqueResult().toString());

        if( count < PurchaseNum ){
            return ServerReturnValue.SERVERRETURNERROR;
        }

        String sql = "update " + DBNmae + " set userId=? where userId='0' and PurchaseType=? limit ?";

        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserID);
        query.setParameter(1, PurchaseType);
        query.setParameter(2, PurchaseNum);
        int result = query.executeUpdate();
        if( result == 0 || PurchaseNum != result ){
            return ServerReturnValue.SERVERRETURNERROR;
        }


        //刷新资金信息
        switch (PurchaseType) {
            case Config.PURCHASELOCALTYRANTS:
                if( updateDynamicActivityLinesPeoples(InstallmentActivityID, Lines) == 0 ){
                    return ServerReturnValue.SERVERRETURNERROR;
                }
                break;
            case Config.PURCHASEPRICKSILK:
                if( updateDynamicActivityLines( InstallmentActivityID,Lines ) == 0 ){
                    return ServerReturnValue.SERVERRETURNERROR;
                }
                break;
            default:
                return ServerReturnValue.SERVERRETURNERROR;
        }

        return ServerReturnValue.SERVERRETURNCOMPELETE;
    }

    public int updateActivityLinesPeoples( String ActivityId, int Lines ){
        String sql = "update activityverifycomplete set CurLinePeoples = CurLinePeoples+? ,curFund = curFund+? where activityId = ? and CurLinePeoples+? <= TotalLinePeoples ";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, Lines);
        query.setParameter(2, ActivityId);
        query.setParameter(3, Lines);
        return query.executeUpdate();
    }

    public int updateActivityLines( String ActivityId,int Lines ){
        String sql = "update activityverifycomplete set CurLines = CurLines+? ,curFund = curFund+? where activityId = ? and CurLines+? <= TotalLines ";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, Lines);
        query.setParameter(2, ActivityId);
        query.setParameter(3, Lines);
        return query.executeUpdate();
    }

    private int updateDynamicActivityLinesPeoples( String InstallmentActivityID, int Lines ){
        String sql = "update activitydynamic set activityCurLinesPeoples = activityCurLinesPeoples+? where activityStageId = ? and activityCurLinesPeoples+? <= activityTotalLinesPeoples ";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, InstallmentActivityID);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

    private int updateDynamicActivityLines( String InstallmentActivityID, int Lines ){
        String sql = "update activitydynamic set activityCurLines = activityCurLines+? where activityStageId = ? and activityCurLines+? <= activityTotalLines ";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, InstallmentActivityID);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

}
