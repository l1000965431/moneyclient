package com.money.controller;

import com.money.Service.AuditActivity.ServiceAuditActivity;
import com.money.Service.ServiceFactory;
import com.money.model.ActivityVerifyModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 项目审核
 * <p>User: Guo Hong
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */
@Controller
@RequestMapping("/AuditActivity")
public class AuditActivityController extends ControllerBase implements IController{
    @RequestMapping("getOneActivity")
    @ResponseBody
    public String getOneActivity( HttpServletRequest request, HttpServletResponse response ){
        ServiceAuditActivity serviceAuditActivity = ServiceFactory.getService("ServiceAuditActivity");
        if( serviceAuditActivity == null ){
            return "failure";
        }
        ActivityVerifyModel activityVerifyModel = serviceAuditActivity.getNewestActivity();

        activityVerifyModel = serviceAuditActivity.getOldestActivity();

        List<ActivityVerifyModel> list = serviceAuditActivity.getActivityList(true);

        serviceAuditActivity.setActivityToGroup();
        return "success";
    }

    @RequestMapping("setActivityAuditResult")
    @ResponseBody
    public String setActivityAuditResult( HttpServletRequest request, HttpServletResponse response ){
        ServiceAuditActivity serviceAuditActivity = ServiceFactory.getService("ServiceAuditActivity");
        if( serviceAuditActivity == null ){
            return "failure";
        }
        serviceAuditActivity.setActivityAuditorResult(1l, ActivityVerifyModel.STATUS_AUDITOR_PASS);

        return "";
    }
}
