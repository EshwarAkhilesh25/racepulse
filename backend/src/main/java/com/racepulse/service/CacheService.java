package com.racepulse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

  private static final Logger log = LoggerFactory.getLogger(CacheService.class);

  @Autowired(required = false)
  private RedisTemplate<String, Object> redisTemplate;

  private final Map<String, CacheEntry> localFallbackCache = new ConcurrentHashMap<>();

  private static class CacheEntry {
    final Object value;
    final long expiryTime;

    CacheEntry(Object value, long ttlMs) {
      this.value = value;
      this.expiryTime = System.currentTimeMillis() + ttlMs;
    }

    boolean isExpired() {
      return System.currentTimeMillis() > expiryTime;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key, Class<T> clazz) {
    if (redisTemplate != null) {
      try {
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
          log.debug("Redis cache HIT for key: {}", key);
          return (T) val;
        }
      } catch (Exception e) {
        log.warn("Redis read error for key {}: {}. Falling back to local cache.", key, e.getMessage());
      }
    }

    CacheEntry entry = localFallbackCache.get(key);
    if (entry != null) {
      if (entry.isExpired()) {
        localFallbackCache.remove(key);
      } else {
        log.debug("Local cache HIT for key: {}", key);
        return (T) entry.value;
      }
    }

    return null;
  }

  public void put(String key, Object value, Duration ttl) {
    if (redisTemplate != null) {
      try {
        redisTemplate.opsForValue().set(key, value, ttl);
        log.debug("Stored in Redis key: {} with TTL: {}", key, ttl);
      } catch (Exception e) {
        log.warn("Redis write error for key {}: {}. Storing in local cache.", key, e.getMessage());
      }
    }
    localFallbackCache.put(key, new CacheEntry(value, ttl.toMillis()));
  }

  public void evict(String key) {
    if (redisTemplate != null) {
      try {
        redisTemplate.delete(key);
      } catch (Exception e) {
        log.warn("Redis evict error for key {}: {}", key, e.getMessage());
      }
    }
    localFallbackCache.remove(key);
  }
}
