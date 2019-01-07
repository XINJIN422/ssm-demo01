package com.yangkang.ssmdemo01.mq;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * MyKafkaMqProducer
 *
 * @author yangkang
 * @date 2019/1/6
 */
public class MyKafkaMqProducer<K, T>{

    private KafkaTemplate<K, T> kafkaTemplate;

    public void setKafkaTemplate(KafkaTemplate<K, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Integer partition, Long timestamp, K key, T data){
//        <1> 若指定Partition ID,则PR被发送至指定Partition
//        <2> 若未指定Partition ID,但指定了Key, PR会按照hasy(key)发送至对应Partition
//        <3> 若既未指定Partition ID也没指定Key，PR会按照round-robin模式发送到每个Partition
//        <4> 若同时指定了Partition ID和Key, PR只会发送到指定的Partition (Key不起作用，代码逻辑决定)
//        key的作用是,在不指定partition情况下,保证每一个相同key的消息发送到同一个partition,
//        这样就可以控制在分布式情况下,消息也有先后顺序;
        ListenableFuture<SendResult<K, T>> listenableFuture = kafkaTemplate.send(topic, partition, timestamp, key, data);
        //发送成功回调
        SuccessCallback<SendResult<K, T>> successCallback = new SuccessCallback<SendResult<K, T>>() {
            @Override
            public void onSuccess(SendResult<K, T> ktSendResult) {
                System.out.println("Kafka发送成功!!!!!!!");
            }
        };
        //发送失败回调
        FailureCallback failureCallback = new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
        listenableFuture.addCallback(successCallback, failureCallback);
    }
}
