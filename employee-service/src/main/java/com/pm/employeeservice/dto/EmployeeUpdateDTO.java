package com.pm.employeeservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeUpdateDTO {

    @Size(max = 100, message = "Name cannot hold more than 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

    private String role;

    private String department_id;

    private Boolean enabled;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    private String dateOfBirth;
}