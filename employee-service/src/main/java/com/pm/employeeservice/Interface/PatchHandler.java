package com.pm.employeeservice.Interface;

import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;

public interface PatchHandler {
     default void apply(Employee employee, Object value) {

    }
    default void apply(Family family, Object value){

    }
    default void apply(Department department, Object value){

    }
}
