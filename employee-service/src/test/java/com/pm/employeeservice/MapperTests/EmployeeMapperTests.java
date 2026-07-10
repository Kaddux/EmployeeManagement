package com.pm.employeeservice.MapperTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.mapper.EmployeeMapper;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeMapperTests {

    Employee createFullEmployee(){
        Department department = new Department();
        department.setDepartment_id(99);
        department.setDepartment_name("Engineering");
        department.setDepartment_code("ENG");

        Employee e = new Employee();
        e.setDepartment(department);
        e.setRole(Role.ROLE_ADMIN);
        e.setName("TEST NAME");
        e.setId(UUID.randomUUID());
        e.setAddress("123 Main St");
        e.setEmail("john@example.com");
        e.setDate_of_birth(LocalDate.of(1990, 5, 15));
        return e;
    }

    @Test
    void toDTO_mapsAllFields() {
        Employee e = createFullEmployee();
        EmployeeResponseDTO dto = EmployeeMapper.toDTO(e);

        assertEquals(e.getId().toString(), dto.getId());
        assertEquals(e.getName(), dto.getName());
        assertEquals(e.getAddress(), dto.getAddress());
        assertEquals(e.getEmail(), dto.getEmail());
        assertEquals(e.getRole().toString(), dto.getRole());
        assertEquals(e.getDate_of_birth().toString(), dto.getDateOfBirth());
        assertEquals(e.getDepartment().getDepartment_id().toString(), dto.getDepartment_id());
        assertEquals(e.getDepartment().getDepartment_name(), dto.getDepartment_name());
        assertEquals(e.getDepartment().getDepartment_code(), dto.getDepartment_code());
    }

    @Test
    void toDTO_whenDepartmentNull_doesNotThrow() {
        Employee e = createFullEmployee();
        e.setDepartment(null);
        assertDoesNotThrow(() -> EmployeeMapper.toDTO(e));
        EmployeeResponseDTO dto = EmployeeMapper.toDTO(e);
        assertNull(dto.getDepartment_id());
        assertNull(dto.getDepartment_name());
        assertNull(dto.getDepartment_code());
    }

    @Test
    void toModel_mapsAllFields() {
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setName("Jane Doe");
        dto.setAddress("456 Oak Ave");
        dto.setEmail("jane@example.com");
        dto.setRole("ROLE_ADMIN");
        dto.setDateOfBirth("1992-08-20");

        Employee e = EmployeeMapper.toModel(dto);

        assertEquals(dto.getName(), e.getName());
        assertEquals(dto.getAddress(), e.getAddress());
        assertEquals(dto.getEmail(), e.getEmail());
        assertEquals(Role.ROLE_ADMIN, e.getRole());
        assertEquals(LocalDate.of(1992, 8, 20), e.getDate_of_birth());
        assertNull(e.getPassword());
        assertNull(e.getDepartment());
    }
}