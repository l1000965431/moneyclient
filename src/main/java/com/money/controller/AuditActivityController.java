package com.money.controller;

import com.money.Service.AuditActivity.ServiceAuditActivity;
import com.money.Service.GroupActivity.ServiceGroupActivity;
import com.money.Service.ServiceFactory;
import com.money.config.Config;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

/**
 * 项目审核
 * <p>User: Guo Hong
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */
@Controller
@RequestMapping("/AuditActivity")
public class AuditActivityController extends ControllerBase implements IController {

    @Autowired
    ServiceAuditActivity serviceAuditActivity;

    @Autowired
    ServiceGroupActivity serviceGroupActivity;

    /**
     * 获取未审批的项目列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getNoAudiActivity")
    @ResponseBody
    public String getNoAudiActivity(HttpServletRequest request, HttpServletResponse response) {


        return "";
    }


    /**
     * 获取已审批的项目列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getHasAudiActivity")
    @ResponseBody
    public String getHasAudiActivity(HttpServletRequest request, HttpServletResponse response) {


        return "";
    }

    @RequestMapping("setActivityAuditResult")
    @ResponseBody
    public String setActivityAuditResult(HttpServletRequest request, HttpServletResponse response) {
        ServiceAuditActivity serviceAuditActivity = ServiceFactory.getService("ServiceAuditActivity");
        if (serviceAuditActivity == null) {
            return Config.SERVICE_FAILED;
        }

        if( serviceAuditActivity.setActivityAuditorResult(4l, ActivityVerifyModel.STATUS_AUDITOR_PASS, "") ){
            return Config.SERVICE_SUCCESS;
        }else {
            return Config.SERVICE_FAILED;
        }
    }

    @RequestMapping("splitActivity")
    @ResponseBody
    public String splitActivity(HttpServletRequest request, HttpServletResponse response){
        //serviceGroupActivity.splitActivityByStage();

        return "";
    }

}
