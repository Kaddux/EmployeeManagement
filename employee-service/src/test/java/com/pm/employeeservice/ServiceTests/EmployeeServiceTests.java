package com.pm.employeeservice.ServiceTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.Exceptions.EmailAlreadyExistsException;
import com.pm.employeeservice.Exceptions.EmployeeNotFoundException;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.dto.EmployeePatchDTO;
import com.pm.employeeservice.dto.EmployeeUpdateDTO;
import com.pm.employeeservice.mail.CreateRegistrationEvent;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.service.CachingService;
import com.pm.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    @Mock
    EmployeeRepository employeeRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock
    DepartmentRepository departmentRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    CachingService cacheService;
    @Mock
    Map<String, PatchHandler<Employee>> handlers;
    @InjectMocks
    EmployeeService employeeService;

    private EmployeeCreateDTO validCreateDTO(){
        EmployeeCreateDTO response = new EmployeeCreateDTO();
        response.setName("TEST NAME");
        response.setAddress("TEST ADDRESS");
        response.setEmail("testmail@example.com");
        response.setRole(String.valueOf(Role.ROLE_EMPLOYEE));
        response.setDateOfBirth("2005-02-02");
        response.setDepartment_id("1");
        response.setPassword("plain-password");

        return response;
    }
    private Employee validEmployee() {
        Department dept = new Department(); dept.setDepartment_id(1);
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setName("John Doe");
        e.setEmail("john@example.com");
        e.setPassword("encoded");
        e.setRole(Role.ROLE_EMPLOYEE);
        e.setEnabled(false);
        e.setDate_of_birth(LocalDate.of(1990, 1, 1));
        e.setAddress("123 St");
        e.setDepartment(dept);
        return e;
    }
    // ---- createUser ----

    @Test
    void createUser_whenEmailExists_throws() {
        EmployeeCreateDTO dto = validCreateDTO();
        when(employeeRepository.existsByEmailAndEnabled(dto.getEmail(), false)).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.createUser(dto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createUser_whenDepartmentNotFound_throws() {
        EmployeeCreateDTO dto = validCreateDTO();
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> employeeService.createUser(dto));
    }

    @Test
    void createUser_success_returnsDTOAndPublishesEvent() {
        EmployeeCreateDTO dto = validCreateDTO();
        Employee employee = validEmployee();
        Department dept = new Department(); dept.setDepartment_id(1);

        when(employeeRepository.existsByEmailAndEnabled(anyString(), anyBoolean())).thenReturn(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(employeeRepository.save(any())).thenReturn(employee);

        var result = employeeService.createUser(dto);

        assertNotNull(result);
        verify(eventPublisher).publishEvent(any(CreateRegistrationEvent.class));
    }

    // ---- getUsers ----

    @Test
    void getUsers_negativePage_throws() {
        assertThrows(ResponseStatusException.class, () -> employeeService.getUsers(-1, 10));
    }

    @Test
    void getUsers_zeroSize_throws() {
        assertThrows(ResponseStatusException.class, () -> employeeService.getUsers(0, 0));
    }

    @Test
    void getUsers_validParams_returnsPage() {
        Page<Employee> page = new PageImpl<>(List.of(validEmployee()));
        when(employeeRepository.findByEnabledTrue(any(Pageable.class))).thenReturn(page);
        var result = employeeService.getUsers(0, 10);
        assertEquals(1, result.getTotalElements());
    }

    // ---- getUserById with caching ----

    @Test
    void getUserById_cacheHit_returnsCachedDTOWithoutDBLookup() {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO cachedDto = new EmployeeResponseDTO();
        cachedDto.setId(id.toString());
        cachedDto.setName("Cached Name");

        when(cacheService.get(id)).thenReturn(cachedDto);

        EmployeeResponseDTO result = employeeService.getUserById(id);

        assertEquals("Cached Name", result.getName());
        verify(cacheService).get(id);
        verify(cacheService, never()).put(any(), any());
        verify(employeeRepository, never()).findById(any());
    }

    @Test
    void getUserById_cacheMiss_fetchesFromDBAndPutsInCache() {
        UUID id = UUID.randomUUID();
        Employee employee = validEmployee();
        employee.setId(id);

        when(cacheService.get(id)).thenReturn(null);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getUserById(id);

        assertNotNull(result);
        verify(cacheService).get(id);
        verify(employeeRepository).findById(id);
        verify(cacheService).put(eq(id), any(EmployeeResponseDTO.class));
    }

    @Test
    void getUserById_employeeNotFound_throwsException() {
        UUID id = UUID.randomUUID();

        when(cacheService.get(id)).thenReturn(null);
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getUserById(id));
        verify(cacheService, never()).put(any(), any());
    }

    // ---- updateEmployee evicts cache ----

    @Test
    void updateEmployee_evictsCacheForUpdatedEmployee() {
        UUID id = UUID.randomUUID();
        Employee employee = validEmployee();
        employee.setId(id);
        Department dept = new Department();
        dept.setDepartment_id(1);

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setAddress("New Address");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setDepartment_id("1");
        updateDTO.setDateOfBirth("1990-01-01");

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("updated@example.com", id)).thenReturn(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));
        when(employeeRepository.save(any())).thenReturn(employee);

        employeeService.updateEmployee(id, updateDTO);

        verify(cacheService).evict(id);
    }

    // ---- deleteEmployee evicts cache ----

    @Test
    void deleteEmployee_evictsCacheBeforeDelete() {
        UUID id = UUID.randomUUID();

        employeeService.deleteEmployee(id);

        verify(cacheService).evict(id);
        verify(employeeRepository).deleteById(id);
    }

    // ---- patchEmployee evicts cache ----

    @Test
    void patchEmployee_evictsCacheAfterPatch() {
        UUID id = UUID.randomUUID();
        Employee employee = validEmployee();
        employee.setId(id);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        employeeService.patchEmployee(id, new EmployeePatchDTO());

        verify(cacheService).evict(id);
    }

}
