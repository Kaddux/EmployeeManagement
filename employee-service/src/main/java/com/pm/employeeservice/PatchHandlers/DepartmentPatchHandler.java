package com.pm.employeeservice.PatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component("department")
public class DepartmentPatchHandler implements PatchHandler {
    @Override
    public void apply(Employee employee, Object value) {
        employee.setDepartment((Department) value);
    }
}
