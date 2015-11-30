package com.money.job;

import com.money.Service.ServiceFactory;
import com.money.Service.activity.ActivityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import until.ScheduleJob;

/**
 * 项目开始作业
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

/**
 * jobName 为项目ID
 */

public class ActivityStartJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ActivityService activityService = ServiceFactory.getService( "ActivityService" );
        ScheduleJob scheduleJob = (ScheduleJob)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        activityService.ActivityCompleteStart( scheduleJob.getJobName() );
    }
}
