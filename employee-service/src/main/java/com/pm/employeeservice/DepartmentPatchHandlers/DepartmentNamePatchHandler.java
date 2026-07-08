package com.pm.employeeservice.DepartmentPatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Department;
import org.springframework.stereotype.Component;

@Component("department_name")
public class DepartmentNamePatchHandler implements PatchHandler {
    @Override
    public void apply(Department department, Object value) {
        department.setDepartment_name((String) value);
    }
}
