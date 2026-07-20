package com.pm.employeeservice.Interface;

public interface PatchHandler<T> {
    void apply(T target, Object value);
}
