package com.money.controller;

import com.money.Service.GroupActivity.ServiceGroupActivity;
import com.money.model.ActivityDetailModel;
import com.money.model.ActivityDynamicModel;
import com.money.model.ActivityGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Created by liumin on 15/7/22.
 */

@Controller
@RequestMapping("/ActivityBeginController")
public class ActivityBeginController extends ControllerBase implements IController {

    @Autowired
    ServiceGroupActivity serviceGroupActivity;

    /**
     * 项目分组上线 生成项目和项目组的参数
     * @param request
     * @param response
     * @return
     */
    public String ActivityBegin( HttpServletRequest request, HttpServletResponse response ){

        //前端发过来的分组项目

        Set<ActivityDynamicModel> activityDynamicModels = null;

        ActivityGroupModel activityGroupModel = serviceGroupActivity.createActivityGroup("", activityDynamicModels);

        serviceGroupActivity.generateGroupTickets( activityGroupModel );

        return "";
    }



}
