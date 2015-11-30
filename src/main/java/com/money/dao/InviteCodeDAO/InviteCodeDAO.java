package com.money.dao.InviteCodeDAO;

import com.money.dao.BaseDao;
import com.money.model.InviteCodeModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import until.MoneyServerDate;

import java.text.ParseException;

/**
 * Created by liumin on 15/10/4.
 */

@Repository
public class InviteCodeDAO extends BaseDao {


    /**
     * 插入邀请码
     *
     * @param num 插入的数量
     * @return
     */
    public int InsertnviteCode(int num) {
        int numTemp = num;
        while (true) {
            if (numTemp <= 1000) {
                insert(numTemp);
                return 1;
            } else {
                insert(1000);
                numTemp -= 1000;
            }
        }
    }


    private void insert(int num) {
        String Sql = "insert into invitecode( inviteCode,userId ) values ";
        String Vaules = "";
        for (int i = 0; i < num; i++) {
            int curTime = (int) System.currentTimeMillis()+i;
            String a = Integer.toHexString(curTime);
            Vaules += "('" + a + "','0'),";
        }
        Vaules = Vaules.substring(0,Vaules.length()-1);
        Sql += Vaules;
        SQLQuery sqlQuery = this.getNewSession().createSQLQuery( Sql );
        sqlQuery.executeUpdate();
        //this.getSession().flush();
    }

    /**
     * 使用邀请码
     *
     * @param userId
     * @throws ParseException
     */
    public int userInviteCode(String userId,String InviteCode) throws ParseException {

        String Sql = "select * from invitecode where userId='0' and inviteCode=? ;";
        Session session = this.getNewSession();

        SQLQuery sqlQuery = session.createSQLQuery(Sql).addEntity(InviteCodeModel.class);
        sqlQuery.setParameter( 0,InviteCode );
        InviteCodeModel inviteCodeModel = (InviteCodeModel) sqlQuery.uniqueResult();

        if (inviteCodeModel != null) {
            inviteCodeModel.setUseDate(MoneyServerDate.getDateCurDate());
            inviteCodeModel.setUserId(userId);
            this.updateNoTransaction(inviteCodeModel);
            return 1;
        }else{
            return 0;
        }

    }

    /**
     * 统计没使用的邀请码还有多少个
     */
    public int countInviteCodeNum() {
        String Sql = "select count(Id) from invitecode where userId='0';";
        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(Sql);
        return Integer.valueOf(sqlQuery.uniqueResult().toString());

    }


}
