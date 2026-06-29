package com.pm.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FamilyResponseDTO {
    private UUID familyId;
    private String father_name;
    private String mother_name;
    private String employee_name;
}
