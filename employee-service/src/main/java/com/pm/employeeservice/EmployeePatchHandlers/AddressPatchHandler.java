package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component("address")
public class AddressPatchHandler implements PatchHandler<Employee> {
    @Override
    public void apply(Employee employee, Object value) {
        employee.setAddress((String) value);
    }
}
