package com.money.controller;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import com.money.Service.user.UserService;
import com.money.config.MoneyServerMQ_Topic;
import com.money.job.ActivityPreferentialStartJob;
import com.money.job.TestJob;
import com.money.model.UserModel;
import org.quartz.DateBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import until.GsonUntil;
import until.MoneyServerDate;
import until.QuartzUntil;
import until.ScheduleJob;

import javax.servlet.http.HttpServletRequest;
import java.io.InterruptedIOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumin on 15/10/15.
 */

@Controller
@RequestMapping("/ActivityPreferentialController")
public class ActivityPreferentialController extends ControllerBase implements IController {

    @Autowired
    ActivityPreferentialService activityPreferentialService;

    @Autowired
    UserService userService;

    @RequestMapping("/getActivityPreferentialInfo")
    @ResponseBody
    public String getActivityPreferentialInfo( HttpServletRequest request ){
        int page = Integer.valueOf(request.getParameter( "page" ));
        int findNum = Integer.valueOf(request.getParameter( "findNum" ));
        return activityPreferentialService.getactivityPreferentialInfo( page,findNum );
    }



    @RequestMapping("/JoinActivityPreferentialInfo")
    @ResponseBody
    public int JoinActivityPreferentialInfo( HttpServletRequest request ){

        String UserId = request.getParameter("userId");
        String ActivityId = request.getParameter("activityId").replace( ".0","" );

        UserModel userModel = userService.getUserInfo( UserId );
        if( userModel == null ){
            return 0;
        }

        Map<String,String> map = new HashMap<>();
        map.put( "userId",UserId );
        map.put( "activityId",ActivityId );
        map.put( "uerExp",Integer.toString(userModel.getUserExp()));
        String messageBody = GsonUntil.JavaClassToJson(map);

        MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_JOINACTIVITYPREFERENTIAL_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_JOINACTIVITYPREFERENTIAL_TAG, messageBody, UserId));

        return 1;
    }

    @RequestMapping("/InsertActivityPreferential")
    @ResponseBody
    public int InsertActivityPreferential( HttpServletRequest request ){
        int ActivityLines = Integer.valueOf(request.getParameter("activityLines"));
        int WinningChance = Integer.valueOf(request.getParameter("winningChance"));
        Date StartTime = MoneyServerDate.StrToDate(request.getParameter("startTime"));
        String ActivityCompeleteId = request.getParameter("activityCompeleteId");
        String LinesEarnings = request.getParameter("linesEarnings");

        int ActivityId = activityPreferentialService.InsertActivityPreferential( ActivityLines,StartTime,
                ActivityCompeleteId,LinesEarnings,WinningChance );

        //开始任务
        ScheduleJob job = new ScheduleJob();
        job.setJobGroup("ActivityPreferential");
        job.setJobId("ActivityPreferential" + Integer.toString(ActivityId));
        job.setJobName("QuartzActivityPreferential_" + Integer.toString(ActivityId));
        job.setDesc( Integer.toString( ActivityId ) );
        job.setJobStatus( "1" );

        try {
            QuartzUntil.getQuartzUntil().AddTick(job, ActivityPreferentialStartJob.class,StartTime );
            return 1;
        } catch (SchedulerException e) {
            return 0;
        }
    }

}
