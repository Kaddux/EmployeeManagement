package com.pm.employeeservice.PatchHandlers;

import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("DateOfBirth")
public class DateOfBirthPatchHandler implements PatchHandler {
    @Override
    public void apply(Employee employee, Object value) {
        employee.setDate_of_birth(LocalDate.parse((String) value));
    }
}
