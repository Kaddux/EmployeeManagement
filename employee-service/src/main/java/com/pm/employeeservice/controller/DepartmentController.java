package com.pm.employeeservice.controller;


import com.pm.employeeservice.dto.DepartmentRequestDTO;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.pm.employeeservice.service.DepartmentService;

import java.math.BigInteger;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDTO> postDepartment(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO){

        DepartmentResponseDTO departmentResponseDTO = departmentService.createDepartment(departmentRequestDTO);


        return ResponseEntity.ok().body(departmentResponseDTO);
    }

    @DeleteMapping("/{department_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDTO> deleteDepartment(@PathVariable Integer department_id){
         departmentService.deleteDepartment(department_id);

        return ResponseEntity.noContent().build();
    }
    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDTO> patchDepartment(BigInteger department_id,
                                                                 @Valid @RequestBody DepartmentRequestDTO updates){
        DepartmentResponseDTO response =departmentService.patchDepartment(department_id,updates);

        return ResponseEntity.ok().body(response);
    }
}
