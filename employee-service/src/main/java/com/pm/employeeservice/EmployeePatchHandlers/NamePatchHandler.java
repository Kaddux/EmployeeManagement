package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component("name")
public class NamePatchHandler implements PatchHandler<Employee> {
    @Override
    public void apply(Employee employee, Object value) {
        employee.setName((String) value);
    }
}
