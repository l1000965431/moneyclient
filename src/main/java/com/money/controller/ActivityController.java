package com.money.controller;

import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 项目控制
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Controller
@RequestMapping("/ActivityController")
public class ActivityController extends ControllerBase implements IController {

    @RequestMapping( "/getActivityDetails" )
         @ResponseBody
         public String getActivityDetails( HttpServletRequest request, HttpServletResponse response ){

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if( activityService == null ){
            return "";
        }else{
            try{
                ActivityDetailModel activityModel = activityService.getActivityDetails(1);
                String Json = GsonUntil.JavaClassToJson(activityModel);
                return Json;
            }catch ( Exception e ){
                return "";
            }
        }
    }

    @RequestMapping( "/getActivityDynamic" )
    @ResponseBody
    public String getActivityDynamic( HttpServletRequest request, HttpServletResponse response ){

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if( activityService == null ){
            return "";
        }else{
            try{
                ActivityDynamicModel activityModel = activityService.getActivityDynamic(1);
                String Json = GsonUntil.JavaClassToJson(activityModel);
                return Json;
            }catch ( Exception e ){
                return "";
            }
        }
    }
}
