package com.money.Service.InviteCodeService;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.InviteCodeDAO.InviteCodeDAO;
import com.money.dao.TransactionSessionCallback;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Created by liumin on 15/10/4.
 */

@Service("InviteCodeService")
public class InviteCodeService extends ServiceBase implements ServiceInterface {

    @Autowired
    InviteCodeDAO inviteCodeDAO;

    public int AddInviteCode(final int num) {

        final int[] state = new int[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                state[0] = inviteCodeDAO.InsertnviteCode(num);
                return true;
            }
        });
        return state[0];
    }

    public int CountInviteCode() {
        final int[] num = new int[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                num[0] = inviteCodeDAO.countInviteCodeNum();
                return true;
            }
        });
        return num[0];
    }

    public int useInviteCode(final String userID, final String inviteCode) throws ParseException {
        final int[] state = new int[1];
        inviteCodeDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                state[0] = inviteCodeDAO.userInviteCode(userID, inviteCode);
                return true;
            }
        });


/*        if( inviteCodeDAO.countInviteCodeNum() < 200 ){
            //插入邀请码消息


        }*/

        return state[0];
    }

}
