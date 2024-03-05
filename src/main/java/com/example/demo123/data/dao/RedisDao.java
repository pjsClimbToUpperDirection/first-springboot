package com.example.demo123.data.dao;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

// redis 접근 관련 로직 집합으로서 구현
@Repository
public class RedisDao {
    // Class RedisTemplate<K,V>
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Integer duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, Duration.ofSeconds(duration));
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return null;
        }
        return (String) values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void setExpireTime(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    // 초기 hash 자료형 설정 시 사용가능
    public void setHashOperations(String key, String hashKey, String value) {
        // HashOperations<H,HK,HV>
        HashOperations<String, String, String> values = redisTemplate.opsForHash();
        values.put(key, hashKey, value);
    }

    public String getHashOperations(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) values.get(key, hashKey) : "";
    }

    public void deleteHashOperations(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }
}
