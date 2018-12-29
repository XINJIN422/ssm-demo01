package com.yangkang.ssmdemo01.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

/**
 * MyRedisClientConfigFactoryBean
 *
 * @author yangkang
 * @date 2018/12/27
 */
public class MyRedisClientConfigFactoryBean implements FactoryBean<JedisClientConfiguration> {

    private GenericObjectPoolConfig poolConfig;

    @Override
    public JedisClientConfiguration getObject() throws Exception {
        return JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build();
    }

    @Override
    public Class<?> getObjectType() {
        return JedisClientConfiguration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
}
