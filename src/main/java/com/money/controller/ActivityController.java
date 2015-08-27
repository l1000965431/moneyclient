package com.money.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import com.money.Service.order.OrderService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.model.*;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.Adapter.InvestInfoAdapter;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
                int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                int numPerPage = Integer.parseInt(request.getParameter("numPerPage"));
                List<ActivityDetailModel> activityModels = activityService.getAllActivityDetail(pageIndex, numPerPage);
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
        String UserID = request.getParameter("userID");
        String Token = request.getParameter("token");
        int page = Integer.valueOf(request.getParameter("page"));
        int findNum = Integer.valueOf(request.getParameter("findNum"));

        UserService userService = ServiceFactory.getService("UserService");
        OrderService orderService = ServiceFactory.getService("OrderService");

        List ActivityHasEarnings = orderService.getOrderByUserID(UserID, page, findNum);
        String Json = GsonUntil.JavaClassToJson(ActivityHasEarnings);
        return Json;
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
        ActivityVerifyCompleteModel completeModel = activityService.getActivityInformation(activityId);
        if (completeModel == null) {
            response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_NO_ACTIVITY);
            return "";
        }
        String Json = GsonUntil.JavaClassToJson(completeModel);
        response.setHeader("response", ServerReturnValue.ACTIVITY_INFO_SUCCESS);
        return Json;
    }

    @RequestMapping("/getActivityInvestInfo")
    @ResponseBody
    public String getActivityInvestInfo(HttpServletRequest request, final HttpServletResponse response) {
        final String activityStageId = request.getParameter("ActivityStageId");
        final String[] Json = new String[1];
        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                ActivityService activityService = ServiceFactory.getService("ActivityService");

                ActivityDetailModel activityDetailModel = activityService.getActivityInvestInfo(activityStageId);
                ActivityDynamicModel activityDynamicModel = activityDetailModel.getDynamicModel();
                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                if (activityDetailModel == null) {
                    response.setHeader("response", ServerReturnValue.ACTIVITY_INVEST_INFO_FAILED);
                    return false;
                }

                //String json = GsonUntil.JavaClassToJson( activityDetailModel.getSrEarningModels() );
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("TotalLines", activityDynamicModel.getActivityTotalLines());
                map.put("TotalLinePeoples", activityDynamicModel.getActivityTotalLinesPeoples());
                map.put("EarningPeoples", GsonUntil.jsonToJavaClass(activityVerifyCompleteModel.getEarningPeoples(), new TypeToken<List>() {
                }.getType()));
                map.put("SREarning", activityDetailModel.getActivityVerifyCompleteModel().getSrEarningModels());
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
     * @param response
     * @return
     */
    @RequestMapping("/getActivityEarnings")
    @ResponseBody
    public String getActivityEarnings(HttpServletRequest request, final HttpServletResponse response) {
        final String UserID = request.getParameter("userID");
        final int Page = Integer.valueOf(request.getParameter("page"));
        final int FindNum = Integer.valueOf(request.getParameter("findNum"));
        final List<Object> ListJson = new ArrayList<Object>();

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                List<UserEarningsModel> userEarningsModelList = session.createCriteria(UserEarningsModel.class)
                        .setMaxResults(FindNum)
                        .setFirstResult(Page * FindNum)
                        .addOrder(Order.desc("UserEarningsDate"))
                        .add(Restrictions.eq("UserID", UserID))
                        .list();
                ActivityService activityService = ServiceFactory.getService("ActivityService");

                for (UserEarningsModel userEarningsModel : userEarningsModelList) {
                    ActivityDetailModel activityDetailModel = activityService.getActivityDetailsNoTran(userEarningsModel.getActivityStageId());
                    if (activityDetailModel == null) {
                        return false;
                    }

                    List<String> ActivityChildInfo = new ArrayList<String>();
                    ActivityVerifyCompleteModel activityVerifyCompleteModel = activityDetailModel.getActivityVerifyCompleteModel();
                    ActivityChildInfo.add(activityDetailModel.getActivityStageId());
                    ActivityChildInfo.add(activityVerifyCompleteModel.getName());
                    ActivityChildInfo.add(Integer.toString(activityVerifyCompleteModel.getTotalInstallmentNum()));
                    ActivityChildInfo.add(Integer.toString(activityDetailModel.getStageIndex()));
                    ActivityChildInfo.add(activityVerifyCompleteModel.getActivityId());
                    ActivityChildInfo.add(activityVerifyCompleteModel.getImageUrl());
                    ActivityChildInfo.add(Integer.toString(userEarningsModel.getUserEarningLines()));
                    ActivityChildInfo.add( userEarningsModel.getUserEarningsDate().toString() );
                    ActivityChildInfo.add( Integer.toString(userEarningsModel.getId()) );
                    ListJson.add( ActivityChildInfo );
                }
                return true;
            }
        });

        String Json = GsonUntil.JavaClassToJson(ListJson);
        return Json;
    }


    @RequestMapping("/Test")
    @ResponseBody
    public String Test(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = ServiceFactory.getService("ActivityService");
        int a = Integer.valueOf(request.getParameter("a"));
        try {
            activityService.InstallmentActivityStart("5", a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

}
