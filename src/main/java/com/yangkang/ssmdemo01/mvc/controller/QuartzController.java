package com.yangkang.ssmdemo01.mvc.controller;

import com.yangkang.ssmdemo01.quartz.QuartzManager;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * quartzController
 *
 * @author yangkang
 * @date 2018/12/3
 */
@Controller
@RequestMapping("/quartz")
public class QuartzController {
    private Logger logger = LoggerFactory.getLogger(QuartzController.class);

//    @Resource
    private QuartzManager quartzManager;

    @RequestMapping("/testDynamicQuartz")
    public void testDynamicQuartz(HttpServletRequest request, HttpServletResponse response) throws SchedulerException {
        //关闭所有的定时任务
//        quartzManager.shutdownJobs();
        //测试阻塞一段时间,错过的任务是否是让它错过还是补上 --结论:只会立刻执行最新的一次,之前的不会补上;
//        System.out.println("hhh");
        //删除指定的定时任务及触发器
//        quartzManager.removeJob("jobTask1", null, "jobTrigger1", null);
        //添加定时任务(不能添加已有的触发器或任务)
//        quartzManager.addJob("jobTask2", null, "jobTrigger2", null, MyJob2.class, "13 * * * * ?", "initialParam1", "initialParam2");
        //修改定时任务
//        quartzManager.modifyJobTime("jobTask2", null, "jobTrigger2", null, "0 0/5 * * * ?");
        //获取所有定时任务
//        quartzManager.getAllJobs();
        //获取所有正在运行的任务   --是指正在执行的任务,方法正被调用
//        quartzManager.getAllRunningJobs();
        //立即执行一次某任务 --之后执行的时间还是按照原来的时间间隔来,这一次并不影响
        quartzManager.runJobOnce("jobTask0", null);
    }
}
