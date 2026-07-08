package com.pm.employeeservice.DepartmentPatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Department;
import org.springframework.stereotype.Component;

@Component("department_code")
public class DepartmentCodePatchHandler implements PatchHandler {
    @Override
    public void apply(Department department, Object value) {
        department.setDepartment_code((String) value);
    }
}
