package com.money.controller;

import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.user.User;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 项目控制
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Controller
@RequestMapping("/ActivityController")
public class ActivityController extends ControllerBase implements IController {

    /**
     * 获得项目详情
     * @param request
     * @param response
     * @return
     */
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

    /**
     * 获得项目动态信息
     * @param request
     * @param response
     * @return
     */
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

    /**
     * 获得已经收益的项目
     * @param request
     * @param response
     * @return
     */
    @RequestMapping( "/GetActivityHasEarnings" )
    @ResponseBody
    public String GetActivityHasEarnings( HttpServletRequest request, HttpServletResponse response ){

        //获取UserID;

        User userService = ServiceFactory.getService( "User" );

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if( userService.tokenLand( "","" ) == 0 ){
            return Integer.toString(ServerReturnValue.USERNOTLAND );
        }

        List ActivityHasEarnings = activityService.GetActivityHasEarnings( "" );

        String Json = GsonUntil.JavaClassToJson( ActivityHasEarnings );

        return Json;
    }

    /**
     * 获得已经投资的项目
     * @param request
     * @param response
     * @return
     */
    @RequestMapping( "/GetActivityHasInvestment" )
    @ResponseBody
    public String GetActivityHasInvestment( HttpServletRequest request, HttpServletResponse response ){

        //获取UserID;

        User userService = ServiceFactory.getService( "User" );

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if( userService.tokenLand( "","" ) == 0 ){
            return Integer.toString(ServerReturnValue.USERNOTLAND );
        }

        List ActivityHasEarnings = activityService.GetActivityHasInvestment( "" );

        String Json = GsonUntil.JavaClassToJson( ActivityHasEarnings );

        return Json;
    }



    @RequestMapping( "/Test" )
    @ResponseBody
    public String Test( HttpServletRequest request, HttpServletResponse response ){

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if(activityService.CanelActivity(1) == Config.SERVICE_SUCCESS ){
            return Config.SERVICE_SUCCESS;
        }else{
            return Config.SERVICE_FAILED;
        }
    }

}
