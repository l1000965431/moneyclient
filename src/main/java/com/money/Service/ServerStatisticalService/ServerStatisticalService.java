package com.money.Service.ServerStatisticalService;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transaction;
import java.util.List;

/**
 * Created by seele on 2015/11/2.
 */

@Service("ServerStatisticalService")
public class ServerStatisticalService extends ServiceBase implements ServiceInterface {

    @Autowired
    GeneraDAO generaDAO;

    /**
     * 每日投资人总收益
     * @param Date
     * @return
     */
    public int getTotlaLotterySum(String Date) {
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord where DATE_FORMAT(earningsrecord.EndDate,'%Y-%m-%d') ='DATE';";
        sql = sql.replace("DATE", Date);

        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }


    /**
     * 一共累计总投资额度
     * @return
     */
    public int getTotalInvestment(){
        String sql = "select sum(userearnings.UserEarningLines)as UserLotterySum from userearnings;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }

    /**
     * 一共累计投资次数
     * @return
     */
    public int getTotalInvestmentNum(){
        String sql = "select COUNT(*) as BuyNum FROM activityorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }

    /**
     * 一共给投资人带来多少收益
     * @return
     */
    public int getTotalLottery(){
        String sql = "select sum(earningsrecord.TotalPrize) as LotteryLinesSum from earningsrecord;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }


    /**
     * 平均每人充值多少钱
     * @return
     */
    public float getAverageWallet(){
        String sql = "select (sum(walletorder.WalletLines)/ (SELECT COUNT(id) from user )) as AverageWallet from walletorder;";
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Float> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }

    /**
     * 每日公司营收金额
     * @return
     */
    public int getRevenueWallet( String Date ){
        String sql = "select ( SUM(earningsrecord.TotalFund)- SUM(earningsrecord.TotalPrize) ) from earningsrecord where DATE_FORMAT(earningsrecord.EndDate,'%Y-%m-%d') ='DATE';";
        sql = sql.replace( "DATE",Date );
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }

    /**
     * 每日提交的项目数
     * @return
     */
    public int getActivityVerify( String Date ){
        String sql = "SELECT count(*) as VerifyNum from activityverify where DATE_FORMAT(activityverify.createDate,'%Y-%m-%d') ='DATE';";
        sql = sql.replace( "DATE",Date );
        Session session = generaDAO.getNewSession();
        org.hibernate.Transaction t = session.getTransaction();
        t.begin();
        List<Integer> list =  session.createSQLQuery( sql ).list();
        t.commit();
        return list.get(0);
    }
}
