package com.money.Service.SubmitActivity;

import com.money.Service.ServiceBase;
import com.money.dao.GeneraDAO;
import com.money.model.ActivityVerifyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;


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
        activityModel.setAuditorStatus(ActivityVerifyModel.STATUS_UN_AUDITOR);
        baseDao.save(activityModel);
        return "hahha";
    }

    private ActivityVerifyModel createProject( HttpServletRequest request ){

        String param = request.getParameter("data");
        ActivityVerifyModel activityModel = GsonUntil.jsonToJavaClass(param, ActivityVerifyModel.class);
        baseDao.save(activityModel);

        return activityModel;
    }
}
