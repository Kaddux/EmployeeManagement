package com.pm.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDTO {

    private String id;
    private String name;
    private String email;
    private String role;
    private String dateOfBirth;
    private String address;
    private String department_id;
    private String department_code;
    private String department_name;

}
