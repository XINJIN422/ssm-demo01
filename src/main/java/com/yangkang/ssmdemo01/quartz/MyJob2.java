package com.yangkang.ssmdemo01.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MyJob2
 * 动态传参
 * 这种情况下,默认的是无状态job,即可以并行运行的job;如果需要指定为有状态job,则加上@DisallowConcurrentExecution注释即可
 *
 * @author yangkang
 * @date 2018/11/30
 */
public class MyJob2 implements Job {
    private Logger logger = LoggerFactory.getLogger(MyJob2.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (logger.isInfoEnabled()){
            logger.info("MyJob2在" + new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()) + " 执行了一次!");
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            for (String key : jobDataMap.getKeys())
                logger.info(key + "====" + jobDataMap.getString(key));
        }
    }
}
