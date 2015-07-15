package com.money.controller;

import com.money.Service.ServiceFactory;
import com.money.Service.ServiceSubmitActivity;
import com.money.Service.activity.ActivityService;
import com.money.model.ActivityModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String getActivityDetails( HttpServletRequest request, HttpServletResponse response ){

/*        ActivityService activityService = ServiceFactory.getService("ActivityService");

        ActivityModel activityModel = activityService.getOrderDetails(1);

        if( activityService == null ){
            return "";
        }else{
            String Json = GsonUntil.JavaClassToJson(activityModel);
            return Json;
        }

        ServiceSubmitActivity serviceSubmitActivity;
        serviceSubmitActivity = ServiceFactory.getService("ServiceSubmitActivity");

        return serviceSubmitActivity.submitActivity(request, response);*/

        return "haha";
    }
}
