package com.money.controller;

import com.money.Service.AuditActivity.ServiceAuditActivity;
import com.money.Service.GroupActivity.ServiceGroupActivity;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.config.Config;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyModel;
import com.money.model.SREarningModel;
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
     * @param response
     * @return
     */
    @RequestMapping("/getNoAudiActivity")
    @ResponseBody
    public String getNoAudiActivity(HttpServletRequest request, HttpServletResponse response) {

        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));


        List<ActivityVerifyModel> list = serviceAuditActivity.getAuditingActivityList(pageIndex, pageNum);

        String json = GsonUntil.JavaClassListToJsonList(list);
        return json;
    }

    /**
     *  获取用户所有的提交过的项目
     *
     */
    @RequestMapping("/getUserActivityList")
    @ResponseBody
    public String getUserActivityList(HttpServletRequest request, HttpServletResponse response) {

//        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
//        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        String userId = request.getParameter("userId");


        List<ActivityVerifyModel> list = serviceAuditActivity.getUserActivityList(userId, 0, 1000);

        String json = GsonUntil.JavaClassListToJsonList(list);
        return json;
    }



    /**
     * 获取已审批的项目列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getHasAudiActivity")
    @ResponseBody
    public String getHasAudiActivity(HttpServletRequest request, HttpServletResponse response) {


        return "";
    }

    @RequestMapping("/setActivityAuditResult")
    @ResponseBody
    public String setActivityAuditResult(HttpServletRequest request, HttpServletResponse response) {
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
    public String splitActivity(HttpServletRequest request, HttpServletResponse response){
        String ActivityID = request.getParameter( "ActivityID" );
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));
        serviceGroupActivity.splitActivityByStage( ActivityID,AdvanceNum,PurchaseNum );
        return "1";
    }

    @RequestMapping("/ActivityStart")
    @ResponseBody
    public String ActivityStart(HttpServletRequest request, HttpServletResponse response){
        String ActivityID = request.getParameter( "ActivityID" );
        activityService.ActivityCompleteStart( ActivityID );
        return "1";
    }


    @RequestMapping("/SetActivityInformationEarnings")
    @ResponseBody
    public int SetActivityInformationEarnings(HttpServletRequest request, HttpServletResponse response){
        String ActivityID = request.getParameter( "ActivityID" );
        int AdvanceNum = Integer.valueOf(request.getParameter("AdvanceNum"));
        int PurchaseNum = Integer.valueOf(request.getParameter("PurchaseNum"));

        //Test
        List<SREarningModel> LinesSREarningList = new ArrayList<SREarningModel>();
        List<SREarningModel> LinePeoplesSREarningList = new ArrayList<SREarningModel>();




        for( int i= 0; i < 3; ++i ){
            SREarningModel srEarningModel = new SREarningModel();
            srEarningModel.setEarningPrice( 20 );
            srEarningModel.setEarningType(2);
            srEarningModel.setNum(3);
            LinesSREarningList.add( srEarningModel );
        }

        for( int i= 0; i < PurchaseNum; ++i ){
            SREarningModel srEarningModel = new SREarningModel();
            srEarningModel.setEarningType( 1 );
            srEarningModel.setEarningPrice(1000/ MoneySeverRandom.getRandomNum( 2,10 ));
            srEarningModel.setNum(1);
            LinePeoplesSREarningList.add( srEarningModel );
        }



        String LinesEarnings = GsonUntil.JavaClassToJson(LinesSREarningList); //request.getParameter("LinesEarnings");
        String LinePeoplesEarnings = GsonUntil.JavaClassToJson(LinePeoplesSREarningList);//request.getParameter("LinePeoplesEarnings");
        serviceGroupActivity.SetActivityInformationEarnings(ActivityID,AdvanceNum,PurchaseNum,LinesEarnings,LinePeoplesEarnings );
        return 1;
    }


}
