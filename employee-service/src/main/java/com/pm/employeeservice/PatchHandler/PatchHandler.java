package com.pm.employeeservice.PatchHandler;

public interface PatchHandler<T> {
    void apply(T target, Object value);
}
