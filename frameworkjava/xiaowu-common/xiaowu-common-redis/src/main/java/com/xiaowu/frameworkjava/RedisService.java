package com.xiaowu.frameworkjava;

import com.xiaowu.frameworkjava.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
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
     * 根据提供的键模式去redis查找符合条件的key
     * @param pattern 键模式
     * @return 符合条件的key集合
     */
    public Collection<String> keys(final String pattern) {
    return redisTemplate.keys(pattern);
    }

    /**
     * 重命名 key
     * @param oldKey 旧的key名
     * @param newKey 新的key名
     */
    public void renameKey (final String oldKey, final String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 删除数据
     * @param key 缓存的key值
     * @return true成功 false失败
     */
    public boolean deleteObject (final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个数据
     * @param collection 多个数据对应的缓存的键值
     * @return true成功 false失败
     */
    public boolean deleteObject(final Collection collection) {
        return redisTemplate.delete(collection) > 0;
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

    /**
     * 缓存list数据
     * @param key 缓存的键值
     * @param value 缓存的值
     * @return 添加元素后 redis 列表的长度
     * @param <T> 对象类型
     */
    public <T> long setCacheList (final String key, final List<T> value) {
        Long count = redisTemplate.opsForList().rightPushAll(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 缓存list数据
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @return 添加元素后 redis 列表的长度
     * @param <T> 对象类型
     */
    public <T> long setCacheList (final String key, final List<T> value, Long expireTime,
                                  TimeUnit timeUnit) {
        Long count = redisTemplate.opsForList().rightPushAll(key, value);
        redisTemplate.expire(key, expireTime, timeUnit);
        return count == null ? 0 : count;
    }

    /**
     * 从 list 列表左侧插入数据(头插)
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param <T> 对象类型
     */
    public <T> void leftPushForList (final String key, final T value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从 list 列表右侧插入数据(尾插)
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param <T> 对象类型
     */
    public <T> void rightPushForList (final String key, final T value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从 list 列表左侧移除数据
     * @param key 缓存的键值
     */
    public void leftPopForList (final String key) {
        redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从 list 列表右侧移除数据
     * @param key 缓存的键值
     */
    public void rightPopForList (final String key) {
        redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移除第一个匹配的元素
     * @param key 缓存的键值
     * @param value 值
     * @param <T> 值类型
     */
    public <T> void removeForList (final String key, final T value) {
        //count > 0 从左往右 count < 0 从右往左 count = 0 移除所有
        //count 移除数据个数&删除的方向
        redisTemplate.opsForList().remove(key, 1L, value);
    }

    /**
     * 移除所有匹配的元素
     * @param key 缓存的键值
     * @param value 值
     * @param <T> 值类型
     */
    public <T> void removeAllForList (final String key, final T value) {
        redisTemplate.opsForList().remove(key, 0L, value);
    }

    /**
     * 移除指定列表的所有元素
     * @param key 缓存的键值
     */
    public void removeForAllList(final String key) {
        redisTemplate.opsForList().trim(key, -1, 0);
    }

    /**
     *  修改指定下标数据
     * @param key        key
     * @param index     下标
     * @param newValue  修改后新值
     * @param <T>       值类型
     */
    public <T> void setElementAtIndex(final String key, int index, T newValue) {
        redisTemplate.opsForList().set(key, index, newValue);
    }

    /**
     * 获取缓存的list对象
     * @param key 缓存的键值
     * @param clazz 对象的类型
     * @return 缓存的list对象
     * @param <T> 对象类型
     */
    public <T> List<T> getCacheList(final String key, Class<T> clazz) {
        List list =  redisTemplate.opsForList().range(key, 0, -1);
        return JsonUtil.string2List(JsonUtil.obj2String(list), clazz);
    }

    /**
     * 获取缓存的list对象
     * @param key 缓存的键值
     * @param valueTypeRef 对象模板信息
     * @return 缓存的list对象
     * @param <T> 对象类型
     */
    public <T> List<T> getCacheList(final String key, TypeReference<List<T>> valueTypeRef) {
        List list =  redisTemplate.opsForList().range(key, 0, -1);
        return JsonUtil.string2Obj(JsonUtil.obj2String(list), valueTypeRef);
    }
}
