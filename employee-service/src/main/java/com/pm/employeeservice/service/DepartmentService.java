package com.pm.employeeservice.service;


import com.pm.employeeservice.Exceptions.DepartmentAlreadyExistsException;
import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.dto.DepartmentRequestDTO;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import com.pm.employeeservice.mapper.DepartmentMapper;
import com.pm.employeeservice.model.Department;
import org.springframework.stereotype.Service;
import com.pm.employeeservice.repository.DepartmentRepository;

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

    public DepartmentResponseDTO deleteDepartment(Integer department_id){
        Department department = departmentRepository.findById(department_id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));

        departmentRepository.deleteById(department_id);

        return DepartmentMapper.toDTO(department);
    }
}
