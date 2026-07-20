package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.service.CachingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingServiceTests {

    @Mock
    private RedisTemplate<String, EmployeeResponseDTO> redisTemplate;

    @Mock
    private ValueOperations<String, EmployeeResponseDTO> valueOperations;

    private CachingService cachingService;

    private static final long TTL_MINUTES = 10L;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cachingService = new CachingService(redisTemplate, TTL_MINUTES);
    }

    @Test
    void get_cacheHit_returnsDTOAndRefreshesTTL() {
        UUID id = UUID.randomUUID();
        String expectedKey = "employee:" + id;
        EmployeeResponseDTO cachedDto = new EmployeeResponseDTO();
        cachedDto.setId(id.toString());
        cachedDto.setName("Cached Employee");

        when(valueOperations.get(expectedKey)).thenReturn(cachedDto);
        when(redisTemplate.expire(eq(expectedKey), eq(TTL_MINUTES), eq(TimeUnit.MINUTES)))
                .thenReturn(true);

        EmployeeResponseDTO result = cachingService.get(id);

        assertNotNull(result);
        assertEquals("Cached Employee", result.getName());
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).expire(expectedKey, TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Test
    void get_cacheMiss_returnsNullAndDoesNotRefreshTTL() {
        UUID id = UUID.randomUUID();
        String expectedKey = "employee:" + id;

        when(valueOperations.get(expectedKey)).thenReturn(null);

        EmployeeResponseDTO result = cachingService.get(id);

        assertNull(result);
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any());
    }

    @Test
    void put_storesDTOWithTTL() {
        UUID id = UUID.randomUUID();
        String expectedKey = "employee:" + id;
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());

        cachingService.put(id, dto);

        verify(valueOperations).set(eq(expectedKey), eq(dto), eq(TTL_MINUTES), eq(TimeUnit.MINUTES));
    }

    @Test
    void put_differentIdTypes_generateCorrectKeys() {
        String expectedKey = "employee:42";

        cachingService.put(42, new EmployeeResponseDTO());

        verify(valueOperations).set(eq(expectedKey), any(), anyLong(), any());
    }

    @Test
    void evict_deletesKeyFromRedis() {
        UUID id = UUID.randomUUID();
        String expectedKey = "employee:" + id;

        when(redisTemplate.delete(expectedKey)).thenReturn(true);

        cachingService.evict(id);

        verify(redisTemplate).delete(expectedKey);
    }

    @Test
    void evict_withStringId_deletesCorrectKey() {
        String expectedKey = "employee:abc-123";

        when(redisTemplate.delete(expectedKey)).thenReturn(true);

        cachingService.evict("abc-123");

        verify(redisTemplate).delete(expectedKey);
    }

    @Test
    void cacheHitThenPut_overwritesCachedValue() {
        UUID id = UUID.randomUUID();
        String key = "employee:" + id;
        EmployeeResponseDTO oldDto = new EmployeeResponseDTO();
        oldDto.setName("Old Name");
        EmployeeResponseDTO newDto = new EmployeeResponseDTO();
        newDto.setName("New Name");

        when(valueOperations.get(key)).thenReturn(oldDto);
        when(redisTemplate.expire(eq(key), anyLong(), any())).thenReturn(true);

        EmployeeResponseDTO firstGet = cachingService.get(id);
        assertEquals("Old Name", firstGet.getName());

        cachingService.put(id, newDto);

        verify(valueOperations).set(eq(key), eq(newDto), eq(TTL_MINUTES), eq(TimeUnit.MINUTES));
    }

    @Test
    void cachePutThenEvict_thenGetReturnsNull() {
        UUID id = UUID.randomUUID();
        String key = "employee:" + id;

        cachingService.put(id, new EmployeeResponseDTO());

        when(redisTemplate.delete(key)).thenReturn(true);
        cachingService.evict(id);

        when(valueOperations.get(key)).thenReturn(null);
        EmployeeResponseDTO result = cachingService.get(id);

        assertNull(result);
    }
}
