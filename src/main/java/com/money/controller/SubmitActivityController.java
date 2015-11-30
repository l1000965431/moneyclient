package com.money.controller;

import com.money.Service.SubmitActivity.ServiceSubmitActivity;
import com.money.model.ActivityVerifyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 项目提交
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Controller
@RequestMapping("/SubmitActivity")
public class SubmitActivityController extends ControllerBase implements IController {
    @Autowired
    private ServiceSubmitActivity serviceSubmitActivity;

    @RequestMapping("submitActivity")
    @ResponseBody
    public String commitProject( HttpServletRequest request){
        String param = request.getParameter("data");
        ActivityVerifyModel activityModel = GsonUntil.jsonToJavaClass(param, ActivityVerifyModel.class);
        return serviceSubmitActivity.submitActivity(activityModel);
    }

    @RequestMapping("reeditActivity")
    @ResponseBody
    public String reeditProject(HttpServletRequest request){
        String param = request.getParameter("data");
        ActivityVerifyModel activityModel = GsonUntil.jsonToJavaClass(param, ActivityVerifyModel.class);
        return serviceSubmitActivity.reeditActivity(activityModel);
    }
}
