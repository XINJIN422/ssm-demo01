package com.yangkang.ssmdemo01.redis;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * MyRedisCacheUtil
 * redis缓存操作工具类
 *
 * @author yangkang
 * @date 2018/12/10
 */
public class MyRedisCacheUtil {

    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     * @param key
     * @param time  时间(秒)
     */
    public void expire(String key, long time){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(time, "time不可以为空!");
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取key对应的过期时间
     * @param key
     * @return
     */
    public long getExpire(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     * @param keys  可以传一个或多个键
     * @return
     */
    public long del(String... keys){
        Assert.notEmpty(keys, "keys不可以为空!");
        return redisTemplate.delete(CollectionUtils.arrayToList(keys));
    }

    // ===============字符串string缓存的操作===============start

    /**
     * 根据key获取对应缓存对象
     * @param key
     * @return
     */
    public Object get(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 字符串缓存塞值
     * @param key
     * @param value
     */
    public void set(String key, Object value){
        Assert.notNull(key, "key不可以为空!");
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 字符串缓存塞值并设定过期时间
     * @param key
     * @param value
     * @param time      时间(秒)
     */
    public void set(String key, Object value, long time){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(time, "time不可以为空!");
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * key对应的数字递增指定值
     * @param key
     * @param increment 增加值(必须大于0)
     * @return
     */
    public long incr(String key, long increment){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(increment, "increment不可以为空!");
        if (increment <= 0 )
            throw new RuntimeException("递增值必须大于0");
        return  redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * key对应的数字递减指定值
     * @param key
     * @param decrement
     * @return
     */
    public long decr(String key, long decrement){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(decrement, "decrement不可以为空!");
        if (decrement <= 0 )
            throw new RuntimeException("递减值必须大于0");
        return  redisTemplate.opsForValue().increment(key, -decrement);
    }

    // ===============哈希hash缓存的操作===============start

    /**
     * 获取key对应的hash中fieldName对应的值
     * @param key
     * @param fieldName
     * @return
     */
    public Object hget(String key, String fieldName){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(fieldName, "fieldName不可以为空!");
        return redisTemplate.opsForHash().get(key, fieldName);
    }

    /**
     * 获取key对应hash的所有键与值
     * @param key
     * @return
     */
    public Map<Object, Object> hgetall(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取key对应的hash中fieldNames对应的多个值
     * @param key
     * @param fieldNames
     * @return
     */
    public List<Object> hmget(String key,List<Object> fieldNames){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(fieldNames, "fieldNames不可以为空!");
        return redisTemplate.opsForHash().multiGet(key, fieldNames);
    }

    /**
     * 向key对应的hash里塞一对对应的键值
     * @param key
     * @param fieldName
     * @param value
     */
    public void hset(String key, String fieldName, Object value){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(fieldName, "fieldName不可以为空!");
        redisTemplate.opsForHash().put(key, fieldName, value);
    }

    /**
     * 向key对应的hash里塞多对对应的键值对
     * @param key
     * @param fieldNamesValues
     */
    public void hmset(String key, Map<Object, Object> fieldNamesValues){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(fieldNamesValues, "fieldNamesValues不可以为空!");
        redisTemplate.opsForHash().putAll(key, fieldNamesValues);
    }

    /**
     * 向key对应的hash里塞一对对应的键值,并设定过期时间
     * @param key
     * @param fieldName
     * @param value
     * @param time  过期时间(秒)
     */
    public void hset(String key, String fieldName, Object value, long time){
        hset(key, fieldName, value);
        expire(key, time);
    }

    /**
     * 向key对应的hash里塞多对对应的键值对,并设定过期时间
     * @param key
     * @param fieldNamesValues
     * @param time
     */
    public void hmset(String key, Map<Object, Object> fieldNamesValues, long time){
        hmset(key, fieldNamesValues);
        expire(key, time);
    }

    /**
     * 删除key对应的hash里fieldNames对应的键值对
     * @param key
     * @param fieldNames
     * @return
     */
    public long hdel(String key, String... fieldNames){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(fieldNames, "fieldNames不可以为空!");
        return redisTemplate.opsForHash().delete(key, fieldNames);
    }

    /**
     * 判断key对应的hash里是否有该fieldName对应的键值对
     * @param key
     * @param fieldName
     * @return
     */
    public boolean hexists(String key, String fieldName){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(fieldName, "fieldName不可以为空!");
        return redisTemplate.opsForHash().hasKey(key, fieldName);
    }

    /**
     * 将key对应的hash里fieldName对应的整型字段增加increment或减少-increment
     * @param key
     * @param fieldName
     * @param increment
     * @return
     */
    public long hincrby(String key, String fieldName, long increment){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(fieldName, "fieldName不可以为空!");
        Assert.notNull(increment, "increment不可以为空!");
        return redisTemplate.opsForHash().increment(key, fieldName, increment);
    }

    // ===============列表list缓存的操作===============start

    /**
     * 获取list缓存索引起始的集合(0到-1为全体值)
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> lrange(String key, long start, long end){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(start, "start不可以为空!");
        Assert.notNull(end, "end不可以为空!");
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list缓存长度
     * @param key
     * @return
     */
    public long llen(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引获取list缓存中的值
     * @param key
     * @param index
     * @return
     */
    public Object lindex(String key, long index){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(index, "index不可以为空!");
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 在list的尾部塞一个值
     * @param key
     * @param value
     */
    public void rpush(String key, Object value){
        Assert.notNull(key, "key不可以为空!");
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 在list的头部塞一个值
     * @param key
     * @param value
     */
    public void lpush(String key, Object value){
        Assert.notNull(key, "key不可以为空!");
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 弹出list尾部的一个值
     * @param key
     */
    public void rpop(String key){
        Assert.notNull(key, "key不可以为空!");
        redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 弹出list头部的一个值
     * @param key
     */
    public void lpop(String key){
        Assert.notNull(key, "key不可以为空!");
        redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 在list缓存的头部批量插入值列表
     * @param key
     * @param values
     */
    public void lpush(String key, List<Object> values){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(values, "values不可以为空!");
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 在list缓存的尾部批量插入值列表
     * @param key
     * @param values
     */
    public void rpush(String key, List<Object> values){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(values, "values不可以为空!");
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 根据索引修改list中的值
     * @param key
     * @param index
     * @param value
     */
    public void lset(String key, long index, Object value){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(index, "index不可以为空!");
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 删除值为value的元素count个
     * @param key
     * @param count
     * @param value
     */
    public void lrem(String key, long count, Object value){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(count, "count不可以为空!");
        Assert.notNull(value, "value不可以为空!");
        redisTemplate.opsForList().remove(key, count, value);
    }

    // ===============集合set缓存的操作===============start

    /**
     * 获取set缓存里的全部元素
     * @param key
     * @return
     */
    public Set<Object> smembers(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断value是否是key对应set里的元素
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, Object value){
        Assert.notNull(key, "key不可以为空!");
        Assert.notNull(value, "value不可以为空!");
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 往key对应的set集合批量插入元素
     * @param key
     * @param values
     */
    public void sadd(String key, Object... values){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(values, "values不可以为空!");
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取key对应的set集合元素个数
     * @param key
     * @return
     */
    public long scard(String key){
        Assert.notNull(key, "key不可以为空!");
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 批量删除key对应的set集合中一些元素
     * @param key
     * @param values
     */
    public void srem(String key, Object... values){
        Assert.notNull(key, "key不可以为空!");
        Assert.notEmpty(values, "values不可以为空!");
        redisTemplate.opsForSet().remove(key, values);
    }

    // ===============有序集合zset缓存的操作 以后用到再补===============start

    /**
     * 清空该rdis数据库所有键
     */
    public void flushDb(){
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * 返回该redis数据库有多少键
     * @return
     */
    public long dbSize(){
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.dbSize();
            }
        });
    }
}
