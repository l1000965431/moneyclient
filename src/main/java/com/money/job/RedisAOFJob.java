package com.money.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by liumin on 15/10/23.
 */

public class RedisAOFJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            String[] cmdA = { "sh", "-c", "redis-cli -h 127.0.0.1 -p 6379 bgrewriteaof" };
            Runtime.getRuntime().exec(cmdA);

            System.out.print( "quarz运行检查" );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
