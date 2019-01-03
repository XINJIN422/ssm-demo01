package com.yangkang.ssmdemo01.mq;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * MyRedisMqReceiver,也可以不用实现接口,不过那就需要用一个包装类MessageListenerAdapter来封装它,并指定被调方法
 * redis消息队列的订阅者
 *
 * @author yangkang
 * @date 2019/1/2
 */
public class MyRedisMqReceiver implements MessageListener {

    private StringRedisTemplate stringRedisTemplateMq;

    public void setStringRedisTemplateMq(StringRedisTemplate stringRedisTemplateMq) {
        this.stringRedisTemplateMq = stringRedisTemplateMq;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisSerializer<?> valueSerializer = stringRedisTemplateMq.getValueSerializer();
        Object channel = valueSerializer.deserialize(message.getChannel());
        Object body = valueSerializer.deserialize(message.getBody());
        System.out.println("=====主题: " + channel);
        System.out.println("=====消息内容: " + String.valueOf(body));
    }
}
