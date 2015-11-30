package com.money.controller;

import com.money.Service.AuditActivity.ServiceAuditActivity;
import com.money.Service.GroupActivity.ServiceGroupActivity;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import com.money.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.MoneySeverRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    @Autowired
    ActivityService activityService;

    /**
     * 获取未审批的项目列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/getNoAudiActivity")
    @ResponseBody
    public String getNoAudiActivity(HttpServletRequest request) {

        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));


        List<ActivityVerifyModel> list = serviceAuditActivity.getAuditingActivityList(pageIndex, pageNum);

        return GsonUntil.JavaClassListToJsonList(list);
    }

    /**
     * 获取用户所有的提交过的项目
     */
    @RequestMapping("/getUserActivityList")
    @ResponseBody
    public String getUserActivityList(HttpServletRequest request) {

        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageNum = Integer.parseInt(request.getParameter("numPerPage"));
        String userId = request.getParameter("userId");


        List<ActivityVerifyModel> list = serviceAuditActivity.getUserActivityList(userId, pageIndex, pageNum);

        String json = GsonUntil.JavaClassListToJsonList(list);
        return json;
    }



    /**
     * 获取已审批的项目列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/getHasAudiActivity")
    @ResponseBody
    public String getHasAudiActivity(HttpServletRequest request) {


        return "";
    }

    @RequestMapping("/setActivityAuditResult")
    @ResponseBody
    public String setActivityAuditResult(HttpServletRequest request) {
        ServiceAuditActivity serviceAuditActivity = ServiceFactory.getService("ServiceAuditActivity");
        if (serviceAuditActivity == null) {
            return Config.SERVICE_FAILED;
        }

        long activityId;
        int result;
        String param;
        try{
            activityId = Long.parseLong(request.getParameter("activityId"));
            result = Integer.parseInt(request.getParameter("result"));
            param = request.getParameter("param");
        }catch (Exception e){
            return "paramIncorrect";
        }

        if( serviceAuditActivity.setActivityAuditorResult(activityId, result, param) ){
            return Config.SERVICE_SUCCESS;
        }else {
            return Config.SERVICE_FAILED;
        }
    }

    @RequestMapping("/splitActivity")
    @ResponseBody
    public String splitActivity(HttpServletRequest request) {
        String ActivityID = request.getParameter("ActivityID");
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        int trageFund = Integer.valueOf(request.getParameter("trageFund"));
        serviceGroupActivity.splitActivityByStage(trageFund,ActivityID, AdvanceNum, PurchaseNum);
        return "1";
    }

    @RequestMapping("/ActivityStart")
    @ResponseBody
    public String ActivityStart(HttpServletRequest request) {
        String ActivityID = request.getParameter("ActivityID");
        activityService.ActivityCompleteStart(ActivityID);
        return "1";
    }


    @RequestMapping("/SetActivityInformationEarnings")
    @ResponseBody
    public int SetActivityInformationEarnings(HttpServletRequest request) {
        String ActivityID = request.getParameter("ActivityID");
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        int Lines = Integer.valueOf(request.getParameter("Lines"));
        int LinePeoples = Integer.valueOf(request.getParameter("LinePeoples"));

        String LinesEarnings = request.getParameter("LinesEarnings");
        String LinePeoplesEarnings = request.getParameter("LinePeoplesEarnings");

        serviceGroupActivity.splitActivityByStage( Lines+LinePeoples,ActivityID,AdvanceNum,PurchaseNum );

        serviceGroupActivity.SetActivityInformationEarnings(Lines, LinePeoples, ActivityID,
                AdvanceNum, PurchaseNum, LinesEarnings, LinePeoplesEarnings);

        if( activityService.ActivityCompleteStart(ActivityID) ){
            return 1;
        }else{
            return 0;
        }
    }

    @RequestMapping("/SetActivityInformationEarningsTest")
    @ResponseBody
    public int SetActivityInformationEarningsTest(HttpServletRequest request) {
        String ActivityID = request.getParameter("ActivityID");
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        int Lines = Integer.valueOf(request.getParameter("Lines"));
        int LinePeoples = Integer.valueOf(request.getParameter("LinePeoples"));

        String LinesEarnings = request.getParameter("LinesEarnings");
        String LinePeoplesEarnings = request.getParameter("LinePeoplesEarnings");

        serviceGroupActivity.splitActivityByStage( Lines+LinePeoples,ActivityID,AdvanceNum,PurchaseNum );

        serviceGroupActivity.SetActivityInformationEarnings(Lines,LinePeoples,ActivityID,
                AdvanceNum, PurchaseNum, LinesEarnings, LinePeoplesEarnings);

        if( activityService.ActivityCompleteStartTest(ActivityID) ){
            return 1;
        }else{
            return 0;
        }
    }

    @RequestMapping("/GetAuditorPassActivity")
    @ResponseBody
    public String GetAuditorPassActivity(HttpServletRequest request, HttpServletResponse response) {
        int page = Integer.valueOf(request.getParameter("page"));
        int findNum = Integer.valueOf(request.getParameter("findNum"));
        int status = Integer.valueOf(request.getParameter("status"));
        return serviceAuditActivity.getAuditorPassActivity( page,findNum,status );
    }

    @RequestMapping("/Test")
    @ResponseBody
    public void Test(HttpServletRequest request) {

        String id = request.getParameter("ID");

        List<SREarningModel> LinePeoplesSREarningList = new ArrayList();
        for (int i = 0; i < 3; ++i) {
            SREarningModel srEarningModel = new SREarningModel();
            srEarningModel.setEarningType(1);
            srEarningModel.setEarningPrice(1000 / MoneySeverRandom.getRandomNum(2, 10));
            srEarningModel.setNum(1);
            LinePeoplesSREarningList.add(srEarningModel);
        }

        String json = GsonUntil.JavaClassToJson(LinePeoplesSREarningList);
        int a = 0;
    }

}
