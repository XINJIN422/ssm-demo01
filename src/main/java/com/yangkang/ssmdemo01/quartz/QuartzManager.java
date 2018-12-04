package com.yangkang.ssmdemo01.quartz;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * QuartzManager
 *
 * @author yangkang
 * @date 2018/11/30
 */
public class QuartzManager {
    private Logger logger = LoggerFactory.getLogger(QuartzManager.class);

    private Scheduler scheduler;

    /**
     * 功能:  添加一个定时任务
     * @param jobName           任务名(即配置文件里的id)
     * @param jobGroupName      任务组名(默认为"DEFAULT")
     * @param triggerName       触发器名(即配置文件里的id)
     * @param triggerGroupName  触发器组名(默认为"DEFAULT")
     * @param jobClass          任务的类类型 eg:MyJob2.class
     * @param cronExpression    时间设置表达式
     * @param strs              可变参数 eg:需要传入MyJob2的execute方法的参数
     * @throws SchedulerException
     */
    public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                       Class jobClass, String cronExpression, String... strs) throws SchedulerException {
        // TODO: 2018/12/4  添加之前最好做一下判断:triggerkey是否以存在
        //配置作业任务
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        int index = 0;
        if (strs != null)
            for (String o : strs)
                jobDetail.getJobDataMap().put("param" + index++, o);    //因为MyJob2对象是由quartz创建的,不是由spring来管理的,所以里面不能直接注入spring的对象,可以利用这个向对象传springbean
        //配置触发器
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(triggerName, triggerGroupName);
        triggerBuilder.startNow();
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
        Trigger trigger = triggerBuilder.build();
//        QuartzBean quartzBean = new QuartzBean();
//        quartzBean.setJobName(jobName);
//        quartzBean.setJobGroupName(jobGroupName);
//        quartzBean.setTriggerName(triggerName);
//        quartzBean.setTriggerGroupName(triggerGroupName);
//        quartzBean.setCronExpression(cronExpression);
//        trigger.getJobDataMap().put("scheduleJob", quartzBean);
        //配置调度器
        scheduler.scheduleJob(jobDetail, trigger);
        //启动
        if (!scheduler.isShutdown())
            scheduler.start();
    }

    /**
     * 功能: 修改一个任务的触发时间
     * @param jobName           任务名(即配置文件里的id)
     * @param jobGroupName      任务组名(默认为"DEFAULT")
     * @param triggerName       触发器名(即配置文件里的id)
     * @param triggerGroupName  触发器组名(默认为"DEFAULT")
     * @param cronExpression    时间设置表达式
     * @throws SchedulerException
     */
    public void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                              String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
        if (trigger == null)
            return;
        String cronExpOld = trigger.getCronExpression();
        if (!cronExpOld.equalsIgnoreCase(cronExpression)){
            //方式一:调用 rescheduleJob
//            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
//            triggerBuilder.withIdentity(triggerName, triggerGroupName);
//            triggerBuilder.startNow();
//            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
//            trigger = (CronTrigger)triggerBuilder.build();
//            scheduler.rescheduleJob(triggerKey, trigger);
            //或者
//            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
//            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
//                    .withSchedule(scheduleBuilder).build();
//            scheduler.rescheduleJob(triggerKey, trigger);

            //方式二:先删除旧的Job,再添加新的Job
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
            Class<? extends Job> jobClass = jobDetail.getJobClass();
            removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
            addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cronExpression,"modifiedParam1", "modifiedParam2");
        }
    }

    /**
     * 功能: 移除一个任务
     * @param jobName           任务名(即配置文件里的id)
     * @param jobGroupName      任务组名(默认为"DEFAULT")
     * @param triggerName       触发器名(即配置文件里的id)
     * @param triggerGroupName  触发器组名(默认为"DEFAULT")
     * @throws SchedulerException
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

        scheduler.pauseTrigger(triggerKey);     //停止触发器
        scheduler.unscheduleJob(triggerKey);    //移除触发器
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));      //删除任务
    }

    /**
     * 功能:  获取所有定时任务
     * @return
     * @throws SchedulerException
     */
    public List<QuartzBean> getAllJobs() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<QuartzBean> jobList = new ArrayList<QuartzBean>();
        for(JobKey jobKey  : jobKeys){
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers){
//                QuartzBean job = (QuartzBean) trigger.getJobDataMap().get("scheduleJob");
                AbstractTrigger abstractTrigger = (AbstractTrigger)trigger;
                QuartzBean job = new QuartzBean();
                job.setJobName(abstractTrigger.getJobName());
                job.setJobGroupName(abstractTrigger.getJobGroup());
                job.setTriggerName(abstractTrigger.getName());
                job.setTriggerGroupName(abstractTrigger.getGroup());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * 功能: 获取所有正在运行的任务
     * @return
     * @throws SchedulerException
     */
    public List<QuartzBean> getAllRunningJobs() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<QuartzBean> jobList = new ArrayList<QuartzBean>();
        for (JobExecutionContext executingJob : executingJobs){
            Trigger trigger = executingJob.getTrigger();
//            QuartzBean job = (QuartzBean) trigger.getJobDataMap().get("scheduleJob");
            AbstractTrigger abstractTrigger = (AbstractTrigger)trigger;
            QuartzBean job = new QuartzBean();
            job.setJobName(abstractTrigger.getJobName());
            job.setJobGroupName(abstractTrigger.getJobGroup());
            job.setTriggerName(abstractTrigger.getName());
            job.setTriggerGroupName(abstractTrigger.getGroup());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 功能: 暂停某个任务
     * @param jobName       任务名(即配置文件里的id)
     * @param jobGroupName  任务组名(默认为"DEFAULT")
     * @throws SchedulerException
     */
    public void pauseJob(String jobName, String jobGroupName) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 功能: 恢复某个任务
     * @param jobName       任务名(即配置文件里的id)
     * @param jobGroupName  任务组名(默认为"DEFAULT")
     * @throws SchedulerException
     */
    public void resumeJob(String jobName, String jobGroupName) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 功能: 立即执行某个任务一次
     * @param jobName       任务名(即配置文件里的id)
     * @param jobGroupName  任务组名(默认为"DEFAULT")
     * @throws SchedulerException
     */
    public void runJobOnce(String jobName, String jobGroupName) throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 功能: 启动所有定时任务
     * @throws SchedulerException
     */
    public void startJobs() throws SchedulerException {
        scheduler.start();
    }

    /**
     * 功能: 关闭所有定时任务
     * @throws SchedulerException
     */
    public void shutdownJobs() throws SchedulerException {
        if (!scheduler.isShutdown())
            scheduler.shutdown();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
