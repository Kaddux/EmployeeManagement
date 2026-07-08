package com.pm.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NewEmployeeInfo {
    private UUID id;
    private String email;
    private String username;
}
