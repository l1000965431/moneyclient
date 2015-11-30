package com.money.job;

import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.config.MoneyServerMQ_Topic;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import until.QuartzUntil;
import until.ScheduleJob;

/**
 * Created by liumin on 15/8/25.
 */
public class LotteryPushJob implements Job {
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        String json = scheduleJob.getDesc();

        if (json == null) {
            return;
        }

        MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERYPUSHLIST_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERYPUSHLIST_TAG, json, "收益通知"));



/*        try {
            QuartzUntil.getQuartzUntil().DeleteJob( scheduleJob );
        } catch (SchedulerException e) {
        }*/
    }
}
