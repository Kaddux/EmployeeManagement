package com.pm.employeeservice.service;


import com.pm.employeeservice.Exceptions.DepartmentAlreadyExistsException;
import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.dto.DepartmentRequestDTO;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.mapper.DepartmentMapper;
import com.pm.employeeservice.model.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.DepartmentRepository;
import org.springframework.util.ReflectionUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO){
        if(departmentRepository.existsByDepartment_code(departmentRequestDTO.getDepartment_code()))
            throw new DepartmentAlreadyExistsException("Department already exists");

        Department department = DepartmentMapper.toModel(departmentRequestDTO);

        Department saveDepartment = departmentRepository.save(department);

        return DepartmentMapper.toDTO(saveDepartment);
    }

    public void deleteDepartment(Integer department_id){
        Department department = departmentRepository.findById(department_id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));

        departmentRepository.deleteById(department_id);

        DepartmentMapper.toDTO(department);
    }
    Map<String, PatchHandler> handlers;
    public DepartmentResponseDTO patchDepartment(BigInteger id, DepartmentRequestDTO updates){
        Department department = departmentRepository.findByDepartment_id(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department Not Found"));

        ReflectionUtils.doWithFields(DepartmentRequestDTO.class, field -> {
            field.setAccessible(true);
            try {
                Object value = field.get(updates);
                if(value != null){
                    PatchHandler handler = handlers.get(field.getName());

                    if(handler != null) {
                        handler.apply(department, value);
                    } else {
                        log.warn("No patch handler found for field: {}", field.getName());
                    }
                }
            }catch (IllegalAccessException e){
                log.error("Error accessing field: {}", field.getName(), e);
            }
        });
        departmentRepository.save(department);

        return DepartmentMapper.toDTO(department);
    }
}
