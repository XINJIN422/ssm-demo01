package com.yangkang.ssmdemo01.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MyJob
 * 或者可以实现quartz里的Job接口里的execute方法,这样可以传参数,参见MyJob2
 *
 * @author yangkang
 * @date 2018/11/29
 */
public class MyJob {
    private Logger logger = LoggerFactory.getLogger(MyJob.class);

    public void testCronTask(){
        if (logger.isInfoEnabled())
            logger.info("MyJob在 " + new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()) + " 执行了一次!");
    }
}
