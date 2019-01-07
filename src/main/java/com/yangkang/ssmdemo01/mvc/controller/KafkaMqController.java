package com.yangkang.ssmdemo01.mvc.controller;

import com.yangkang.ssmdemo01.mq.MyKafkaMqProducer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * KafkaMqController
 *
 * @author yangkang
 * @date 2019/1/6
 */
@Controller
@RequestMapping("/kafka")
public class KafkaMqController {

    @Resource
    private MyKafkaMqProducer myKafkaMqProducer;

    @RequestMapping("/testPublishMessage/{topic}/{partition}/{timestamp}/{key}/{data}")
    public void testPublishMessage(@PathVariable("topic")String topic, @PathVariable("partition")Integer partition,
                                   @PathVariable("timestamp")Long timestamp, @PathVariable("key")String key,
                                   @PathVariable("data")String data){
        myKafkaMqProducer.sendMessage(topic, partition, timestamp, key, data);
    }
}
