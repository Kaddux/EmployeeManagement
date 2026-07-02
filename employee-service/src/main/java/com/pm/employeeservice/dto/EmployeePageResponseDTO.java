package com.pm.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeePageResponseDTO<T> {

    public List<T> Employeedata;
    public PageMetaResponse pageMetaResponse;
}
