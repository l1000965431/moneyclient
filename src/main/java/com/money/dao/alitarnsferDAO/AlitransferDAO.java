package com.money.dao.alitarnsferDAO;

import com.money.dao.BaseDao;
import com.money.model.AliTransferInfo;
import com.money.model.AlitransferModel;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import until.MoneyServerDate;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumin on 15/9/25.
 */

@Repository
public class AlitransferDAO extends BaseDao {

    /**
     * 提交提现申请
     *
     * @param UserId   用户ID
     * @param Lines    提款额度
     * @param RealName 真实姓名
     * @param AliName  支付宝帐号
     * @return 0失败 >0成功
     */
    public int Submitalitansfer(String UserId, int Lines, String RealName, String AliName) {

        AlitransferModel alitransferModel = GetAliTransfer(UserId);

        if (alitransferModel == null) {

            AlitransferModel NewalitransferModel = new AlitransferModel();
            NewalitransferModel.setUserId(UserId);
            NewalitransferModel.setAliEmail(AliName);
            NewalitransferModel.setRealName(RealName);
            NewalitransferModel.setLines(Lines);
            try {
                NewalitransferModel.setAlitransferDate( MoneyServerDate.getDateCurDate() );
            } catch (ParseException e) {
                return 0;
            }
            this.saveNoTransaction(NewalitransferModel);
            return 1;
        } else {
            String sql = "update alitransfer set TransferLines = TransferLines+?, AlitransferDate=?1 where UserId = ?";
            Session session = this.getNewSession();
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, Lines);
            query.setParameter(1, MoneyServerDate.getStringCurDate());
            query.setParameter(2, UserId);
            return query.executeUpdate();
        }
    }


    public AlitransferModel GetAliTransfer(String UserId) {

        final AlitransferModel alitransferModel;

        alitransferModel = (AlitransferModel) getNewSession().createCriteria(AlitransferModel.class)
                .setMaxResults(1)
                .add(Restrictions.eq("UserId", UserId))
                .uniqueResult();

        return alitransferModel;

    }

    /**
     * 提现成功清零
     *
     * @param UserId 用户ID
     * @return 0:错误 >0:成功
     */
    public int Clearalitansfer(String UserId) throws ParseException {
        String sql = "update alitransfer set Lines = 0 , AlitransferDate=? where UserId = ?";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, UserId);
        query.setDate(1, MoneyServerDate.getDateCurDate());
        return query.executeUpdate();
    }

    /**
     * 设置支付失败
     *
     * @param UserId 用户ID
     */
    public void SetalitransferFailed(String UserId) {

        AlitransferModel alitransferModel = GetAliTransfer(UserId);

        if (alitransferModel == null) {
            return;
        }
        alitransferModel.setIsFaliled(true);

        this.updateNoTransaction(alitransferModel);
    }


    public int GetCountGrouping() {
        String sql = "select count(Id) from alitransfer where Faliled != false";
        Session session = this.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        return Integer.valueOf(query.uniqueResult().toString());
    }

    /**
     * 获得提现申请订单 每3000个位一组
     *
     * @return null:失败
     */
    public List GetAliTransferOdrer() {

        int page = 0;
        List list = new ArrayList();
        Session session = this.getNewSession();
        String sql = "SELECT sum(TransferLines) as TransferLines,count(id) as CountTransfer " +
                "FROM (select TransferLines,Id from alitransfer where TransferLines != 0 and IsFaliled != true limit ? ,3000) as TransferTemp;";
        Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(AliTransferInfo.class));

        while (true) {
            query.setParameter(0, page);
            List<AliTransferInfo> Sqllist = (List<AliTransferInfo>)query.list();

            if (Sqllist == null || Sqllist.size() == 0 || Sqllist.get(0).getCountTransfer().equals( BigInteger.ZERO) ) {
                return list;
            }

            list.add( Sqllist );
            page += 3000;
        }
    }

    /**
     * 获得对应的批次信息
     *
     * @param page 获取的页数
     * @return 此次批次的列表
     */
    public List<AlitransferDAO> GetAliTransferInfo(int page) {
        Session session = this.getNewSession();
        String sql = "select * from alitransfer where TransferLines != 0 and IsFaliled != true limit ? ,3000;";
        Query query = session.createSQLQuery(sql).addEntity(AlitransferModel.class);

        query.setParameter(0, page);
        List<AlitransferDAO> Sqllist = (List<AlitransferDAO>)query.list();

        return Sqllist;
    }

}
