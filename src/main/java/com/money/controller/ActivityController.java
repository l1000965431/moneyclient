package com.money.controller;

import com.google.gson.GsonBuilder;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.order.OrderService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.Adapter.InvestInfoAdapter;
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
     * 获得项目列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getActivityDetails")
    @ResponseBody
    public String getActivityDetails(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return "";
        } else {
            try {
                List<ActivityDetailModel> activityModels = activityService.getAllActivityDetail();
                String json = GsonUntil.getGson().toJson(activityModels);
                return json;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 获得项目动态信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getActivityDynamic")
    @ResponseBody
    public String getActivityDynamic(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return "";
        } else {
            try {
                ActivityDynamicModel activityModel = activityService.getActivityDynamic("1");
                String Json = GsonUntil.JavaClassToJson(activityModel);
                return Json;
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * 获得已经收益的项目
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/GetActivityHasEarnings")
    @ResponseBody
    public String GetActivityHasEarnings(HttpServletRequest request, HttpServletResponse response) {

        //获取UserID;

        UserService userService = ServiceFactory.getService("userService");

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (userService.tokenLand("", "") == 0) {
            return Integer.toString(ServerReturnValue.USERNOTLAND);
        }

        List ActivityHasEarnings = activityService.GetActivityHasEarnings("");

        String Json = GsonUntil.JavaClassToJson(ActivityHasEarnings);

        return Json;
    }

    /**
     * 获得已经投资的项目
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/GetActivityHasInvestment")
    @ResponseBody
    public String GetActivityHasInvestment(HttpServletRequest request, HttpServletResponse response) {
        //获取UserID;
        String UserID = request.getParameter("UserID");
        String Token = request.getParameter("Token");
        int page = Integer.valueOf(request.getParameter("Page"));
        int findNum = Integer.valueOf(request.getParameter("findNum"));

        UserService userService = ServiceFactory.getService("UserService");
        OrderService orderService = ServiceFactory.getService("OrderService");

        if (userService.tokenLand(UserID,Token) == 0) {
            return Integer.toString(ServerReturnValue.USERNOTLAND);
        }

        List ActivityHasEarnings = orderService.getOrderByUserID(UserID,page,findNum );
        String Json = GsonUntil.JavaClassToJson(ActivityHasEarnings);
        return Json;
    }

    /**
     * 获取项目详情
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/GetActivityInformation")
    @ResponseBody
    public String getActivityInformation(HttpServletRequest request, HttpServletResponse response) {

        // 项目分期id
        String activityId = request.getParameter("activityId");

        ActivityService activityService = ServiceFactory.getService("ActivityService");


        ActivityVerifyCompleteModel completeModel = activityService.getActivityInformation(activityId);
        if(completeModel == null){
            response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_NO_ACTIVITY);
            return "";
        }

        String Json = GsonUntil.JavaClassToJson(completeModel);
        response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_SUCCESS);

        return Json;
    }

    @RequestMapping("/getActivityInvestInfo")
    @ResponseBody
    public String getActivityInvestInfo(HttpServletRequest request, HttpServletResponse response) {
        String activityStageId = request.getParameter("ActivityStageId");
        ActivityService activityService = ServiceFactory.getService("ActivityService");

        ActivityDetailModel activityDetailModel = activityService.getActivityInvestInfo(activityStageId);
        if(activityDetailModel == null){
            response.setHeader("response", ServerReturnValue.ACTIVITY_INVEST_INFO_FAILED);
            return "";
        }

        String json = new GsonBuilder().registerTypeAdapter(ActivityDetailModel.class, new InvestInfoAdapter()).create()
                .toJson(activityDetailModel);
        response.setHeader("response", ServerReturnValue.ACTIVITY_INVEST_INFO_SUCCESS);
        return json;
    }


    @RequestMapping("/Test")
    @ResponseBody
    public String Test(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        int a = Integer.valueOf(request.getParameter( "a" ));

        try {
            activityService.InstallmentActivityStart("5",a );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1";
    }

}
