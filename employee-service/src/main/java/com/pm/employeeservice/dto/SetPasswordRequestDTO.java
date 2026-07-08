package com.pm.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPasswordRequestDTO {
    private String token;
    private String newPassword;
}
