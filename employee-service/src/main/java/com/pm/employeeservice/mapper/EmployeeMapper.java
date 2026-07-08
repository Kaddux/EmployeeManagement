package com.pm.employeeservice.mapper;

import com.pm.employeeservice.dto.AdminOnlyDTO;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.dto.EmployeeUpdateDTO;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.Enum.Role;

import java.time.LocalDate;

public class EmployeeMapper {
    public static EmployeeResponseDTO toDTO(Employee employee){
        EmployeeResponseDTO EmployeeDTO = new EmployeeResponseDTO();
        EmployeeDTO.setId(employee.getId().toString());
        EmployeeDTO.setName(employee.getName());
        EmployeeDTO.setAddress(employee.getAddress());
        EmployeeDTO.setEmail(employee.getEmail());
        if (employee.getDepartment() != null) {
            EmployeeDTO.setDepartment_id(employee.getDepartment().getDepartment_id().toString());
            EmployeeDTO.setDepartment_name(employee.getDepartment().getDepartment_name());
            EmployeeDTO.setDepartment_code(employee.getDepartment().getDepartment_code());
        }
        EmployeeDTO.setRole(employee.getRole().toString());
        EmployeeDTO.setDateOfBirth(employee.getDate_of_birth().toString());

        return EmployeeDTO;
    }

    //converting EmployeeCreateDTO to an entity model
    public static Employee toModel(EmployeeCreateDTO employeeCreateDTO){
        Employee employee = new Employee();
        employee.setName(employeeCreateDTO.getName());
        employee.setAddress(employeeCreateDTO.getAddress());
        employee.setEmail(employeeCreateDTO.getEmail());
        employee.setRole(Role.valueOf(employeeCreateDTO.getRole()));
        // Department setting is handled in the service layer where DB access is available
        employee.setDate_of_birth(LocalDate.parse(employeeCreateDTO.getDateOfBirth()));

        return employee;
    }
}
