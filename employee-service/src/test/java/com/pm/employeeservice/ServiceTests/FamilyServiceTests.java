package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.dto.FamilyRequestDTO;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.FamilyRepository;
import com.pm.employeeservice.service.FamilyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyServiceTests {

    @Mock FamilyRepository familyRepository;
    @Mock EmployeeRepository employeeRepository;
    @InjectMocks FamilyService familyService;

    @Test
    void createFamily_success() {
        UUID empId = UUID.randomUUID();
        Employee emp = new Employee();
        emp.setId(empId);
        emp.setName("John");

        FamilyRequestDTO dto = new FamilyRequestDTO();
        dto.setFather_name("Bob");
        dto.setMother_name("Alice");
        dto.setNumber_of_members(3);

        Family saved = new Family();
        saved.setFamily_id(UUID.randomUUID());
        saved.setFather_name("Bob");
        saved.setMother_name("Alice");
        saved.setEmployee(emp);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(familyRepository.save(any())).thenReturn(saved);

        var result = familyService.createFamily(empId, dto);
        assertEquals("Bob", result.getFather_name());
        assertEquals("Alice", result.getMother_name());
        assertEquals("John", result.getEmployee_name());
    }

    @Test
    void createFamily_employeeNotFound_throws() {
        UUID empId = UUID.randomUUID();
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> familyService.createFamily(empId, new FamilyRequestDTO()));
        verify(familyRepository, never()).save(any());
    }

    @Test
    void getFamily_returnsList() {
        Family f = new Family();
        f.setFamily_id(UUID.randomUUID());
        when(familyRepository.findAll()).thenReturn(List.of(f));
        var result = familyService.getFamily();
        assertEquals(1, result.size());
    }

    @Test
    void getUniqueFamily_returnsDTO() {
        UUID empId = UUID.randomUUID();
        Employee emp = new Employee();
        emp.setId(empId);
        emp.setName("Jane");

        Family f = new Family();
        f.setFamily_id(UUID.randomUUID());
        f.setFather_name("Bob");
        f.setEmployee(emp);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(emp));
        when(familyRepository.findByEmployeeId(empId)).thenReturn(f);

        var result = familyService.getUniqueFamily(empId);
        assertNotNull(result);
        assertEquals("Bob", result.getFather_name());
    }

    @Test
    void getUniqueFamily_employeeNotFound_throws() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> familyService.getUniqueFamily(id));
    }
}
