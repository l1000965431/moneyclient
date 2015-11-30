package com.money.job;

import com.money.Service.ServiceFactory;
import com.money.Service.activityPreferential.ActivityPreferentialService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import until.ScheduleJob;

/**
 * 特惠项目开始Job
 * <p>User: 刘旻
 * <p>Date: 15-7-13
 * <p>Version: 1.0
 */

public class ActivityPreferentialStartJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        int ActivityId = Integer.valueOf(scheduleJob.getDesc());
        ActivityPreferentialService activityPreferentialService = ServiceFactory.getService("ActivityPreferentialService");
        activityPreferentialService.StartActivityPreferential( ActivityId );
    }
}
