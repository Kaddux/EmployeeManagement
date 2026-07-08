package com.pm.employeeservice.Interface;

import com.pm.employeeservice.model.Employee;

public interface PatchHandler {
     default void apply(Employee employee, Object value) {

    }
}
