package com.pm.employeeservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import com.pm.employeeservice.dto.EmployeeResponseDTO;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, EmployeeResponseDTO> redisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            System.out.println("Attempting to connect to Redis...");
            EmployeeResponseDTO dummyDto = new EmployeeResponseDTO();
            redisTemplate.opsForValue().set("test-key", dummyDto);
            EmployeeResponseDTO value = redisTemplate.opsForValue().get("test-key");
            
            System.out.println("Successfully connected to Redis! Value retrieved: " + value);
            assertNotNull(value);
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis!");
            e.printStackTrace();
            throw e;
        }
    }
}
