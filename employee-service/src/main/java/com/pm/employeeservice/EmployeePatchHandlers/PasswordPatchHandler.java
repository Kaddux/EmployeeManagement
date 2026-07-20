package com.pm.employeeservice.EmployeePatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("password")
public class PasswordPatchHandler implements PatchHandler<Employee> {

    private final PasswordEncoder passwordEncoder;

    public PasswordPatchHandler(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void apply(Employee employee, Object value) {
        employee.setPassword(passwordEncoder.encode((String) value));
    }
}
