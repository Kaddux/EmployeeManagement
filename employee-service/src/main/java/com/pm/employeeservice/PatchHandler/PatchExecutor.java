package com.pm.employeeservice.PatchHandler;

import com.pm.employeeservice.Interface.PatchHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

@Slf4j
@Component
public class PatchExecutor {

    @SuppressWarnings("unchecked")
    public <T> void apply(T target, Object patchDto, Map<String, ?> handlers) {
        ReflectionUtils.doWithFields(patchDto.getClass(), field -> {
            field.setAccessible(true);
            try {
                Object value = field.get(patchDto);
                if (value != null) {
                    PatchHandler<T> handler = (PatchHandler<T>) handlers.get(field.getName());
                    if (handler != null) {
                        handler.apply(target, value);
                    } else {
                        log.warn("No patch handler found for field: {}", field.getName());
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field: {}", field.getName(), e);
            }
        });
    }
}
