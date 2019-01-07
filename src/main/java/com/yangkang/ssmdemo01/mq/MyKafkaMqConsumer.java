package com.yangkang.ssmdemo01.mq;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 * MyKafkaMqConsumer
 *
 * @author yangkang
 * @date 2019/1/6
 */
public class MyKafkaMqConsumer implements MessageListener<String, String> {
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        //正常情况下,是局部有序的;不过因为重试机制,有可能A发送失败后,重试,到了B之后
        //如果要保证严格的顺序,可以在broker设置max.in.flight.requests.per.connection为1,并且consumer的enable.auto.commit设为false
        //意义是:发送状态未知的最大消息数为1,每一条消息完全处理成功后再手动提交状态,之后再发送另一条消息
        //此时需要实现带有Consumer入参的方法,调用Consumer.commitSync()方法手动提交;
        System.out.println("kafkaConsumer收到消息了===============");
        System.out.println("topic=" + data.topic() + "; value=" + data.value() + ";");
    }
}
