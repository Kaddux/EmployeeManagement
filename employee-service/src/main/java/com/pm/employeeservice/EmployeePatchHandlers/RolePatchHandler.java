package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component("role")
public class RolePatchHandler implements PatchHandler<Employee> {
    @Override
    public void apply(Employee employee, Object value) {
        employee.setRole(Role.valueOf((String) value));
    }
}
