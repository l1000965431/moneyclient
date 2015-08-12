package com.money.dao.TicketDAO;

import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.model.LotteryModel;
import com.money.model.TicketModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import until.MoneySeverRandom;

import java.util.List;
import java.util.UUID;

/**
 * 项目生成票DAO
 * <p>User: 刘旻
 * <p>Date: 15-7-19
 * <p>Version: 1.0
 */

@Repository
public class TicketDAO extends BaseDao {

    /**
     * @param ActivityID 项目ID
     * @param Lines      金额
     * @param number     票的数量
     */
    public void InsertTickDB(int ActivityID, int Lines, int number) {

    }

    /**
     * 创建项目组对应的票的数据表
     *
     * @param ActivityID
     */
    public boolean CreateTicketDBWihActivityID(int ActivityID) {

        return false;
    }

    /**
     * 生成票的序列号
     *
     * @return
     */
    long CreateTicketNumber() {
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        long ticketTime = System.currentTimeMillis();
        return ticketTime ^ random;
    }

    /**
     * 随机获得未获奖的人
     *
     * @return
     */
    public List GetRandNotLottery(int activityID) {

        String sql = "SELECT * FROM sqlrandtset where lottery = 0 order by rand() limit 10000";

        try {
            List list = this.getListClassBySQL(sql, LotteryModel.class);
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean IsLotteryTicket(int ActivityID, long Ticketnum) {

        String sql = "";

        if (this.excuteintBySQL(sql) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 创建票ID
     *
     * @param InstallmentActivityID
     * @param TotalNum
     */
    public void CreateTicketID(String InstallmentActivityID, int TotalNum, int TickType) {
        String DBName = Config.ACTIVITYGROUPTICKETNAME + InstallmentActivityID;
        Session session = this.getNewSession();
        int CurNum = 0;
        while (true) {
            String insertSql = "insert into " + DBName + " (TickID,PurchaseType) values";
            if (TotalNum == CurNum) {
                return;
            }

            String TicketID[] = new String[1000];

            int index = 0;
            for (int i = 0; i < 1000; ++i) {
                TicketID[i] = UUID.randomUUID().toString();
                index++;

                if (index >= TotalNum) {
                    break;
                }
            }

            String ValueSql = new String();
            for (int j = 0; j < index; ++j) {
                ValueSql += "('" + TicketID[j] + "'," + Integer.toString(TickType) + "),";
            }

            ValueSql = ValueSql.substring(0, ValueSql.lastIndexOf(","));
            ValueSql += ";";
            insertSql += ValueSql;
            CurNum += index;
            session.createSQLQuery(insertSql).executeUpdate();
        }


    }
}
