package com.money.Service.SubmitActivity;

import com.money.Service.ServiceBase;
import com.money.config.Config;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.model.ActivityVerifyModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;


/**
 * 项目提交服务
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Service("ServiceSubmitActivity")
public class ServiceSubmitActivity extends ServiceBase {
    @Autowired
    private GeneraDAO baseDao;

    public String submitActivity( ActivityVerifyModel activityModel ){
        activityModel.setAuditorStatus(ActivityVerifyModel.STATUS_FIRST_AUDITING);
        activityModel.setCreateDate(new Date(System.currentTimeMillis()));
        Serializable result = baseDao.save(activityModel);
        if( result == null ){
            return "failure";
        }
        return "success";
    }

    public String reeditActivity( final ActivityVerifyModel model ){
        String ret = baseDao.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                session.update(model);
                return true;
            }
        });

        if( ret.compareTo(Config.SERVICE_FAILED) == 0 ){
            return "failure";
        }

        return "success";
    }

    private ActivityVerifyModel createProject( HttpServletRequest request ){

        String param = request.getParameter("data");
        ActivityVerifyModel activityModel = GsonUntil.jsonToJavaClass(param, ActivityVerifyModel.class);
        baseDao.save(activityModel);

        return activityModel;
    }
}
