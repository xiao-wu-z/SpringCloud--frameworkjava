package com.xiaowu.frameworkjava.service;

import com.xiaowu.frameworkjava.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
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

    /***************************操作list类型*************************************/


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

    /**
     * 根据范围获取缓存的list
     * @param key 缓存的键值
     * @param start 开始位置
     * @param end 结束位置
     * @param clazz 类信息
     * @return 缓存的list
     * @param <T> 对象类型
     */
    public <T> List<T> getCacheListByRange(final String key, int start, int end, Class<T> clazz) {
        List list =  redisTemplate.opsForList().range(key, start, end);
        return JsonUtil.string2List(JsonUtil.obj2String(list), clazz);
    }

    /**
     * 根据范围获取缓存的list(支持复杂的泛型嵌套)
     * @param key 缓存的键值
     * @param start 开始位置
     * @param end 结束位置
     * @param valueTypeRef 对象模板信息
     * @return 缓存的list
     * @param <T> 对象类型
     */
    public <T> List<T> getCacheListByRange(final String key, int start, int end,
                                           TypeReference<List<T>> valueTypeRef) {
        List list =  redisTemplate.opsForList().range(key, start, end);
        return JsonUtil.string2Obj(JsonUtil.obj2String(list), valueTypeRef);
    }

    /**
     * 获取 list 列表的长度
     * @param key 缓存的键值
     * @return list 列表的长度
     * @param <T> 对象类型
     */
    public <T> long getCacheListSize(final String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size == null ? 0 : size;
    }


    /***************************操作set类型*************************************/

    /**
     * set添加元素(批量添加或者单个添加)
     * @param key 缓存的键值
     * @param values 缓存的值
     */
    public void setMember(final String key, final Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * set删除元素(批量删除或者单个删除)
     * @param key 缓存的键值
     * @param values 删除的值
     * @return
     */
    public boolean deleteMember(final String key, final Object... values) {
        return redisTemplate.opsForSet().remove(key, values).longValue() > 0;
    }

    /**
     * 获取缓存的set集合
     * @param key 缓存的键值
     * @param valueTypeRef 对象模板信息
     * @return 缓存的set集合
     * @param <T> 对象类型
     */
    public <T> Set<T> getCacheSet(final String key, TypeReference<Set<T>> valueTypeRef) {
        Set set = redisTemplate.opsForSet().members(key);
        return JsonUtil.string2Obj(JsonUtil.obj2String(set), valueTypeRef);
    }


    /***************************操作zset类型*************************************/


    /**
     * 添加元素到有序集合，如果value已经存在，则更新对应的score值
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param score 排序的score值
     */
    public void addMemberZSet(final String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 移除有序集合中的元素
     * @param key 缓存的键值
     * @param value 缓存的值
     * @return true成功/false失败
     */
    public boolean deleteMemberZSet(final String key, final Object value) {
        return redisTemplate.opsForZSet().remove(key, value).longValue() > 0;
    }

    /**
     * 根据score值移除有序集合中的元素
     * @param key 缓存的键值
     * @param minScore 最小score值
     * @param maxScore 最大score值
     */
    public void removeMemberZSetByScore(final String key, double minScore, double maxScore) {
        redisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);
    }

    /**
     * 降序获取有序集合中的所有元素
     * @param key 缓存的键值
     * @param valueTypeRef 对象模板信息
     * @return 有序集合中的所有元素
     * @param <T> 对象类型
     */
    public <T> Set<T> getCacheZSetDesc(final String key, TypeReference<Set<T>> valueTypeRef) {
        Set set = redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        return JsonUtil.string2Obj(JsonUtil.obj2String(set), valueTypeRef);
    }

    /***************************操作hash类型*************************************/

    /**
     * 缓存map数据
     * @param key 缓存的键值
     * @param dataMap 缓存的值
     * @param <T> 对象类型
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 往Hash中存入单个数据
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     * @param <T> 对象类型
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获取缓存的map数据（支持复杂的泛型嵌套）
     * @param key key
     * @param typeReference 类型模板
     * @return hash对应的map
     * @param <T> 对象类型
     */
    public <T> Map<String, T> getCacheMap(final String key, TypeReference<Map<String, T>> typeReference) {
        Map data= redisTemplate.opsForHash().entries(key);
        return JsonUtil.string2Obj(JsonUtil.obj2String(data), typeReference);
    }


    /**
     * 获取Hash中的单个数据
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     * @param <T> 对象类型
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取Hash中的多个数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @param typeReference 对象模板
     * @return 获取的多个数据的集合
     * @param <T> 对象类型
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys,
                                             TypeReference<List<T>> typeReference) {
        List data = redisTemplate.opsForHash().multiGet(key, hKeys);

        return JsonUtil.string2Obj(JsonUtil.obj2String(data), typeReference);
    }

    /***************************LUA脚本*************************************/

    /**
     * 删除指定值对应的 Redis 中的键值（compare and delete）
     *
     * @param key   缓存key
     * @param value value
     * @return 是否完成了比较并删除
     */
    public boolean cad(String key, String value) {
        if (key.contains(StringUtils.SPACE) || value.contains(StringUtils.SPACE)) {
            return false;
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        // 通过lua脚本原子验证令牌和删除令牌
        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value);
        return !Objects.equals(result, 0L);
    }
}
