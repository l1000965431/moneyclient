package until;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 * <p>User: liumin
 * <p>Date: 15-7-29
 * <p>Version: 1.0
 */

@Component
public class QuartzUntil {

    @Autowired
    protected SchedulerFactoryBean schedulerFactoryBean;

    private static QuartzUntil quartzUntil;

    public static QuartzUntil getQuartzUntil() {
        return quartzUntil;
    }

    QuartzUntil(){
        quartzUntil = this;
    }


    /**
     * 添加任务
     *
     *@param job 作业信息
     */
    public void AddTick(ScheduleJob job, Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        //获取触发器标识
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        //获取触发器trigger
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        if (trigger == null) {//不存在任务

            //创建任务
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(job.getJobName(), job.getJobGroup())
                    .build();

            jobDetail.getJobDataMap().put("scheduleJob", job);


            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job
                    .getCronExpression());

            //按新的cronExpression表达式构建一个新的trigger
                    trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getJobName(), job.getJobGroup())
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            //把任务插入数据库
        }

    }
}
