package com.money.controller;

import com.google.gson.reflect.TypeToken;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.order.OrderService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityVerifyCompleteModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目控制
 * <p>User: liumin
 * <p>Date: 15-7-14
 * <p>Version: 1.0
 */

@Controller
@RequestMapping("/ActivityController")
public class ActivityController extends ControllerBase implements IController {

    @Autowired
    GeneraDAO generaDAO;

    /**
     * 获得项目列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/getActivityDetails")
    @ResponseBody
    public String getActivityDetails(HttpServletRequest request) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return "";
        } else {
            try {
                int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                int numPerPage = Integer.parseInt(request.getParameter("numPerPage"));
                List<ActivityDetailModel> activityModels = activityService.getAllActivityDetail(pageIndex, numPerPage);
                return GsonUntil.getGson().toJson(activityModels);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    @RequestMapping("/getActivityDetailsTest")
    @ResponseBody
    public String getActivityDetailsTest(HttpServletRequest request) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return "";
        } else {
            try {
                int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                int numPerPage = Integer.parseInt(request.getParameter("numPerPage"));
                List<ActivityDetailModel> activityModels = activityService.getAllActivityDetailTest(pageIndex, numPerPage);
                return GsonUntil.getGson().toJson(activityModels);
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
     * @return
     */
    @RequestMapping("/getActivityDynamic")
    @ResponseBody
    public String getActivityDynamic(HttpServletRequest request) {

        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return "";
        } else {
            try {
                ActivityDynamicModel activityModel = activityService.getActivityDynamic("1");
                return GsonUntil.JavaClassToJson(activityModel);
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * 获得已经收益的项目
     *
     * @param request
     * @return
     */
    @RequestMapping("/GetActivityHasEarnings")
    @ResponseBody
    public String GetActivityHasEarnings(HttpServletRequest request) {
        //获取UserID;
        UserService userService = ServiceFactory.getService("userService");
        ActivityService activityService = ServiceFactory.getService("ActivityService");

        if (activityService == null) {
            return null;
        }

/*
        if( !this.UserIsLand( UserID,token ) ){
            return Integer.toString(ServerReturnValue.USERNOTLAND);
        }
*/


        List ActivityHasEarnings = activityService.GetActivityHasEarnings("");
        return GsonUntil.JavaClassToJson(ActivityHasEarnings);
    }

    /**
     * 获得已经投资的项目
     *
     * @param request
     * @return
     */
    @RequestMapping("/GetActivityHasInvestment")
    @ResponseBody
    public String GetActivityHasInvestment(HttpServletRequest request) {
        //获取UserID;
        String UserID = request.getParameter("userID");
        String Token = request.getParameter("token");
        int page = Integer.valueOf(request.getParameter("page"));
        int findNum = Integer.valueOf(request.getParameter("findNum"));

        if (!this.UserIsLand(UserID, Token)) {
            return Config.STRLANDFAILED;
        }

        OrderService orderService = ServiceFactory.getService("OrderService");

        List ActivityHasEarnings = orderService.getOrderByUserID(UserID, page, findNum);
        return GsonUntil.JavaClassToJson(ActivityHasEarnings);
    }

    /**
     * 获取项目详情
     *
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

        if (activityService == null) {
            return null;
        }

        ActivityVerifyCompleteModel completeModel = activityService.getActivityInformation(activityId);
        if (completeModel == null) {
            response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_NO_ACTIVITY);
            return "";
        }
        response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_SUCCESS);
        return GsonUntil.JavaClassToJson(completeModel);
    }

    @RequestMapping("/getActivityInvestInfo")
    @ResponseBody
    public String getActivityInvestInfo(HttpServletRequest request, final HttpServletResponse response) {
        final String activityStageId = request.getParameter("ActivityStageId");
        final String[] Json = new String[1];
        final ActivityService activityService = ServiceFactory.getService("ActivityService");
        if (activityService == null) {
            return null;
        }

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityDetailModel activityDetailModel = activityService.getActivityInvestInfo(activityStageId);
                ActivityDynamicModel activityDynamicModel = activityDetailModel.getDynamicModel();
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();

                Map<String, Object> map = new HashMap();
                map.put("TotalLines", activityDynamicModel.getActivityTotalLines());
                map.put("TotalLinePeoples", activityDynamicModel.getActivityTotalLinesPeoples());
                map.put("EarningPeoples", GsonUntil.jsonToJavaClass(activityVerifyCompleteModel.getEarningPeoples(), new TypeToken<List>() {
                }.getType()));
                map.put("SREarning", activityDetailModel.getActivityVerifyCompleteModel().getSrEarningModels());
                map.put("MaxVirtualSecuritiesBuy",Integer.toString(Config.MaxVirtualSecuritiesBuy));
                Json[0] = GsonUntil.JavaClassToJson(map);

                response.setHeader("response", ServerReturnValue.ACTIVITY_INVEST_INFO_SUCCESS);
                return true;
            }
        });


        return Json[0];
    }

    /**
     * 获得收益项目
     *
     * @param request
     * @return
     */
    @RequestMapping("/getActivityEarnings")
    @ResponseBody
    public String getActivityEarnings(HttpServletRequest request) {
        final String UserID = request.getParameter("userID");
        final String token = request.getParameter("token");
        final int Page = Integer.valueOf(request.getParameter("page"));
        final int FindNum = Integer.valueOf(request.getParameter("findNum"));


        if (!this.UserIsLand(UserID, token)) {
            return Config.STRLANDFAILED;
        }

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        if (activityService == null) {
            return Config.SERVICE_FAILED;
        }

        List<Object> ListJson = activityService.getActivityEarnings( UserID,Page,FindNum );

        return GsonUntil.JavaClassToJson(ListJson);
    }

    /**
     * 更改项目状态
     *
     * @param request
     * @return
     */
    @RequestMapping("/changeActivityStatus")
    @ResponseBody
    public String changeActivityStatus(HttpServletRequest request) {
        String activityId;
        int result;
        try {
            activityId = request.getParameter("activityId");
            result = Integer.parseInt(request.getParameter("status"));
        } catch (Exception e) {
            return "paramIncorrect";
        }

        ActivityService activityService = ServiceFactory.getService("ActivityService");
        if (activityService == null) {
            return Config.SERVICE_FAILED;
        }
        try {
            activityService.changeActivityStatus(activityId, result);
        } catch (Exception e) {
            return Config.SERVICE_FAILED;
        }
        return Config.SERVICE_SUCCESS;
    }


    @RequestMapping("/Test")
    @ResponseBody
    public String Test(HttpServletRequest request) {
        ActivityService activityService = ServiceFactory.getService("ActivityService");
        int a = Integer.valueOf(request.getParameter("a"));
        try {
            assert activityService != null;
            activityService.InstallmentActivityStart("5", a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

}
