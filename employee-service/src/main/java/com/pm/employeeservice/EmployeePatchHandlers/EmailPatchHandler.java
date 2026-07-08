package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component("email")
public class EmailPatchHandler implements PatchHandler {
    public void apply(Employee employee, Object value){
        employee.setEmail((String) value);
    }
}
