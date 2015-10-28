package com.money.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import until.MoneyServerDate;
import until.ScheduleJob;

import java.io.Serializable;

/**
 * Created by liumin on 15/7/29.
 */


public class TestJob implements Job {
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("任务成功运行"+":TestJob1"+ MoneyServerDate.getStringCurDate());
        /*ScheduleJob scheduleJob = (ScheduleJob)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]");*/
    }
}
