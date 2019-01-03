package com.yangkang.ssmdemo01.mvc.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * RedisMqController
 *
 * @author yangkang
 * @date 2019/1/2
 */
@Controller
@RequestMapping("/mq")
public class RedisMqController {

    @Resource
    private StringRedisTemplate stringRedisTemplateMq;

    @RequestMapping("/testPublishMessage/{message}")
    public void testPublishMessage(@PathVariable("message")String message){
        Assert.notNull(message, "message不能为空!");
        //这样发送的消息为即发即失的
        /*如果期望订阅是持久的，那么如下的设计思路可以借鉴（如下原理基于JMS）：
            1) subscribe端首先向一个Set集合中增加“订阅者ID”，此Set集合保存了“活跃订阅”者，订阅者ID标记每个唯一的订阅者，例如：sub:email,sub:web。此SET称为“活跃订阅者集合”
            2) subcribe端开启订阅操作，并基于Redis创建一个以“订阅者ID”为KEY的LIST数据结构，此LIST中存储了所有的尚未消费的消息。此LIST称为“订阅者消息队列”
            3) publish端：每发布一条消息之后，publish端都需要遍历“活跃订阅者集合”，并依次向每个“订阅者消息队列”尾部追加此次发布的消息。(应该先插队列后发布消息)
            4) 到此为止，我们可以基本保证，发布的每一条消息，都会持久保存在每个“订阅者消息队列”中。
            5) subscribe端，每收到一个订阅消息，在消费之后，必须删除自己的“订阅者消息队列”头部的一条记录。
            6) subscribe端启动时，如果发现自己的自己的“订阅者消息队列”有残存记录，那么将会首先消费这些记录，然后再去订阅。
                (如何确保[删除完队列后,subscribe完全启动开始监听前,队列不会再有值进来呢?],考虑这个问题时发现redis作为队列,只适合对数据准确性要求不高的场景使用)
        */
        stringRedisTemplateMq.convertAndSend("topic.channel", message);
        System.out.println("=======发布消息成功!");
    }
}
