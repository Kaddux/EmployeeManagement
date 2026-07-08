package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

@Component("department_id")
public class DepartmentPatchHandler implements PatchHandler {

    private final DepartmentRepository departmentRepository;

    public DepartmentPatchHandler(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void apply(Employee employee, Object value) {
        Department department = departmentRepository.findById(Integer.valueOf((String) value))
                .orElseThrow(() -> new DepartmentNotFoundException("Department Not Found"));
        employee.setDepartment(department);
    }
}
