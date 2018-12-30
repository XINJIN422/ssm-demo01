package com.yangkang.ssmdemo01.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * MyRedisConfig
 *
 * @author yangkang
 * @date 2018/12/27
 */
@Configuration
@EnableCaching
@ImportResource("classpath:spring/cache/spring-rediscache3.xml")
public class MyRedisConfig {

    @Bean
    public StringRedisSerializer keySerializer(){
        return new StringRedisSerializer();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer valueSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory, StringRedisSerializer keySerializer, GenericJackson2JsonRedisSerializer valueSerializer){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.setEnableTransactionSupport(true);
        //在redisTemplate这个bean初始化完成后便塞入MyBatisRedisCache的类变量中,否则mybatis开启使用redis作为二级缓存后,启动会报错!
        MyBatisRedisCache.redisTemplate = redisTemplate;
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory, StringRedisSerializer keySerializer, GenericJackson2JsonRedisSerializer valueSerializer){
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(keySerializer);
        stringRedisTemplate.setValueSerializer(valueSerializer);
        stringRedisTemplate.setHashKeySerializer(keySerializer);
        stringRedisTemplate.setHashValueSerializer(valueSerializer);
        stringRedisTemplate.setEnableTransactionSupport(true);
        return stringRedisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisSerializationContext.SerializationPair<String> stringSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(keySerializer());
        RedisSerializationContext.SerializationPair<Object> valueSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer());

        //默认name为default的缓存
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(300))              //5分钟后过期
                .prefixKeysWith("rediscache3::")                //key前缀
                .serializeKeysWith(stringSerializationPair)     //key序列化器(默认的就是stringSerializationPair,这边可以不配)
                .serializeValuesWith(valueSerializationPair);   //value序列化器
        //定义一个name为users的缓存
        RedisCacheConfiguration redisCacheConfiguration2 = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))              //10分钟后过期
                .prefixKeysWith("[rediscache3-2]")              //key前缀
                .serializeKeysWith(stringSerializationPair)     //key序列化器(默认的就是stringSerializationPair,这边可以不配)
                .serializeValuesWith(valueSerializationPair);   //value序列化器
        //再定义一个name为users2的缓存,其他key前缀与keygenerator和上面相同,看看同时缓存行不行
        //试验结果证明,虽然cachename不同,但是如果前缀和keygenerator相同,那么在同一redis数据库里只有一条记录
        //那么用redisTemplate应该也不用设定cachename,就能进行相应的读写(猜测)
        //试验结果证明,redisTemplate读写不用指定cachename,并且会按照指定序列化方式自动处理对象
        LinkedHashMap<String, RedisCacheConfiguration> initialCacheConfiguration = new LinkedHashMap<>();
        initialCacheConfiguration.put("users", redisCacheConfiguration2);
        initialCacheConfiguration.put("users2", redisCacheConfiguration2);
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration, initialCacheConfiguration);
    }

    @Bean
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName() + ".");
                sb.append(method.getName() + "(");
                for (Object param : objects)
                    sb.append(param.toString() + ", ");
                sb.delete(sb.length()-2, sb.length());
                sb.append(")");
                return sb.toString();
            }
        };
    }

    @Bean
    public KeyGenerator keyGenerator2(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                return "keyGenerator2--" + Arrays.asList(objects);
            }
        };
    }

}
