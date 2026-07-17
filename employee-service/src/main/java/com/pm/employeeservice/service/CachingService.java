package com.pm.employeeservice.service;

import com.pm.employeeservice.dto.EmployeeResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CachingService {
    private static final String CACHE_PREFIX = "employee:";

    private final RedisTemplate<String, EmployeeResponseDTO> redisTemplate;
    private final long ttlMinutes;

    public CachingService(RedisTemplate<String, EmployeeResponseDTO> redisTemplate,
                          @Value("${app.cache.employee.ttl-minutes:10}") long ttlMinutes) {
        this.redisTemplate = redisTemplate;
        this.ttlMinutes = ttlMinutes;
    }

    private String buildKey(Object id) {
        return CACHE_PREFIX + id;
    }

    public EmployeeResponseDTO get(Object id) {
        String key = buildKey(id);
        EmployeeResponseDTO dto = redisTemplate.opsForValue().get(key);
        if (dto != null) {
            redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
        }
        return dto;
    }

    public void put(Object id, EmployeeResponseDTO dto) {
        String key = buildKey(id);
        redisTemplate.opsForValue().set(key, dto, ttlMinutes, TimeUnit.MINUTES);
    }
    public void evict(Object id){
        String key = buildKey(id);
        redisTemplate.delete(key);
    }
}
