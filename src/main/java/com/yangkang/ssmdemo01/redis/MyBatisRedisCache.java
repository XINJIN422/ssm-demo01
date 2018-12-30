package com.yangkang.ssmdemo01.redis;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MyBatisRedisCache
 * 使用redis作为二级缓存
 * 参考了mybatis-redis-1.0.0-beta2.jar!\org\mybatis\caches\redis\RedisCache.class
 *
 * @author yangkang
 * @date 2018/12/29
 */
public final class MyBatisRedisCache implements Cache {

//    private static RedisTemplate<String, Object> redisTemplate =
//            ContextLoader.getCurrentWebApplicationContext().getBean("redisTemplate", RedisTemplate.class);

//    private static MyRedisCacheUtil myRedisCacheUtil;
//            (MyRedisCacheUtil)SpringContextsUtil.getBean("myRedisCacheUtil", MyRedisCacheUtil.class);
//            ContextLoader.getCurrentWebApplicationContext().getBean("myRedisCacheUtil", MyRedisCacheUtil.class);

    //redisTemplate在容器启动该bean初始化后便塞进来,否则启动会报错!
    public static RedisTemplate<Object, Object> redisTemplate;

    private final String id;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MyBatisRedisCache(final String id){
        if (id == null)
            throw new IllegalArgumentException("Cache instances require an ID");
        this.id = id;
//        if (myRedisCacheUtil == null){
//            synchronized (myRedisCacheUtil){
//                if (myRedisCacheUtil == null)
//                    myRedisCacheUtil = ContextLoader.getCurrentWebApplicationContext().getBean("myRedisCacheUtil", MyRedisCacheUtil.class);
//            }
//        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
//        myRedisCacheUtil.hset(this.id, key.toString(), value);
        redisTemplate.opsForHash().put(this.id, key.toString(), value);
    }

    @Override
    public Object getObject(Object key) {
//        return myRedisCacheUtil.hget(this.id, key.toString());
        return redisTemplate.opsForHash().get(this.id, key.toString());
    }

    @Override
    public Object removeObject(Object key) {
//        return myRedisCacheUtil.hdel(this.id, key.toString());
        return redisTemplate.opsForHash().delete(this.id, key.toString());
    }

    @Override
    public void clear() {
//        myRedisCacheUtil.del(this.id);
        redisTemplate.delete(this.id);
    }

    @Override
    public int getSize() {
//        return myRedisCacheUtil.hgetall(this.id).size();
        return redisTemplate.opsForHash().entries(this.id).size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() { return this.readWriteLock; }
}
