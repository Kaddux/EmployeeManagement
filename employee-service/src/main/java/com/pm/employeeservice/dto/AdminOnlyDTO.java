package com.pm.employeeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminOnlyDTO {
    @NotBlank(message = "Role is required")
    private String role;
}
