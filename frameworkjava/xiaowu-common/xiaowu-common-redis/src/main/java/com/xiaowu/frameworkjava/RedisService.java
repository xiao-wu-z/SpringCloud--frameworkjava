package com.xiaowu.frameworkjava;

import com.xiaowu.frameworkjava.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * redis操作工具类
 */
@Component
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置key的过期时间
     * @param key key值
     * @param timeout 过期时间
     * @return true成功 false失败
     */
    public boolean expire (final String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置key的过期时间
     * @param key key值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true成功 false失败
     */
    public boolean expire (final String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取key的过期时间
     * @param key key值
     * @return 过期时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key key值
     * @return true存在 false不存在
     */
    public boolean hasKey (final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 缓存String数据 （将数据转为Json字符串存入）
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param <T> 对象类型
     */
    public <T> void setCacheObject (final String key, final T value) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /**
     * 缓存String数据 （将数据转为Json字符串存入）
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @param <T> 对象类型
     */
    public <T> void setCacheObject (final String key, final T value, Long expireTime, TimeUnit timeUnit) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, expireTime, timeUnit);
    }

    /**
     * 获取缓存String数据
     * @param key 缓存的键值
     * @param value 对象类型
     * @return 缓存的对象
     * @param <T> 对象类型
     */
    public <T> boolean setCacheObjectIfAbsent(final String key, final T value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 获取缓存String数据
     * @param key 缓存的键值
     * @param value 对象类型
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @return 缓存的对象
     * @param <T> 对象类型
     */
    public <T> boolean setCacheObjectIfAbsent(final String key, final T value, Long expireTime,
                                              TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, timeUnit);
    }

    /**
     * 获取缓存数据(将缓存的数据反序列化为指定类型返回)
     * @param key 缓存的键值
     * @param clazz 缓存对象类型
     * @return 缓存的对象
     * @param <T> 缓存对象类型
     */
    public <T> T getCacheObject(final String key, Class<T> clazz) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object o = valueOperations.get(key);
        if (o == null) {
            return null;
        }
        String jsonStr = JsonUtil.obj2String(o);
        return JsonUtil.string2Obj(jsonStr, clazz);
    }

    /**
     * 获取缓存数据(将缓存数据反序列化为指定类型返回,支持复杂的泛型)
     * @param key 缓存的键值
     * @param valueTypeRef 类型模板
     * @return 缓存的对象
     * @param <T> 缓存对象类型
     */
    public <T> T getCacheObject(final String key, TypeReference<T> valueTypeRef) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object o = valueOperations.get(key);
        if (o == null) {
            return null;
        }
        String jsonStr = JsonUtil.obj2String(o);
        return JsonUtil.string2Obj(jsonStr, valueTypeRef);
    }
}
