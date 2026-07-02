package com.pm.employeeservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FamilyPageResponseDTO<T> {
    private List<T> family_data;
    private PageMetaResponse pageMetaResponse;
}
