package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.Exceptions.DepartmentAlreadyExistsException;
import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.dto.DepartmentRequestDTO;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTests {

    @Mock DepartmentRepository departmentRepository;
    @InjectMocks DepartmentService departmentService;

    @Test
    void createDepartment_success() {
        DepartmentRequestDTO dto = new DepartmentRequestDTO();
        dto.setDepartment_name("Engineering");
        dto.setDepartment_code("ENG");

        Department saved = new Department();
        saved.setDepartment_id(1);
        saved.setDepartment_name("Engineering");
        saved.setDepartment_code("ENG");

        when(departmentRepository.save(any())).thenReturn(saved);
        var result = departmentService.createDepartment(dto);

        assertEquals("Engineering", result.getDepartment_name());
        assertEquals("ENG", result.getDepartment_code());
        assertEquals(1, result.getDepartment_id());
    }

    @Test
    void createDepartment_duplicateCode_throws() {
        DepartmentRequestDTO dto = new DepartmentRequestDTO();
        dto.setDepartment_code("ENG");
        when(departmentRepository.existsByDepartment_code("ENG")).thenReturn(true);
        assertThrows(DepartmentAlreadyExistsException.class,
                () -> departmentService.createDepartment(dto));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartment_success() {
        Department dept = new Department();
        dept.setDepartment_id(1);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));
        departmentService.deleteDepartment(1);
        verify(departmentRepository).deleteById(1);
    }

    @Test
    void deleteDepartment_notFound_throws() {
        when(departmentRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class,
                () -> departmentService.deleteDepartment(99));
        verify(departmentRepository, never()).deleteById(any());
    }
}
