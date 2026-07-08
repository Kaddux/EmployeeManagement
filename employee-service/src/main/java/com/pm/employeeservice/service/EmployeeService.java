package com.pm.employeeservice.service;


import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.Exceptions.*;
import com.pm.employeeservice.Interface.PatchHandler;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeePatchDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.dto.EmployeeUpdateDTO;
import com.pm.employeeservice.mail.CreateActivationEvent;
import com.pm.employeeservice.mail.CreateRegistrationEvent;
import com.pm.employeeservice.mail.LimitEmailVerificationRequests;
import com.pm.employeeservice.mapper.EmployeeMapper;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LimitEmailVerificationRequests limitEmailVerificationRequests;

    public EmployeeService(ApplicationEventPublisher applicationEventPublisher, EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository, LimitEmailVerificationRequests limitEmailVerificationRequests, Map<String, PatchHandler> handlers) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.limitEmailVerificationRequests = limitEmailVerificationRequests;
        this.handlers = handlers;
    }

    public Page<EmployeeResponseDTO> getUsers(int page, int size) throws ResponseStatusException {
        if(page < 0 || size <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Pageable pageable = PageRequest.of(page, size);


        return employeeRepository.findByEnabledTrue(pageable).map(EmployeeMapper ::toDTO);

    }
    public EmployeeResponseDTO resendActivationMail(String email){
        Employee employee = employeeRepository.findEmployeeByEmail(email);

      if(employee == null)
          throw new  EmployeeNotFoundException("Employee Not Found");

      if(employee.isEnabled())
          throw new IllegalArgumentException("Employee already enabled");

      limitEmailVerificationRequests.limitEmailVerificationRequested(employee);

        try {
            applicationEventPublisher.publishEvent(new CreateActivationEvent(this, email, employee));
            return EmployeeMapper.toDTO(employee);
        } catch (Exception e) {
            log.warn("Error in sending activation mail {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public EmployeeResponseDTO createUser(EmployeeCreateDTO employeeCreateDTO) {
        if (employeeRepository.existsByEmailAndEnabled(employeeCreateDTO.getEmail(), employeeCreateDTO.isEnabled()))
            throw new EmailAlreadyExistsException("User with this email already exists" + employeeCreateDTO.getEmail());

        Employee newEmployee = EmployeeMapper.toModel(employeeCreateDTO);

        Department dept = departmentRepository.findById(Integer.valueOf(employeeCreateDTO.getDepartment_id()))
                .orElseThrow(() -> new RuntimeException("Department not found"));
        newEmployee.setDepartment(dept);

        newEmployee.setPassword(passwordEncoder.encode(employeeCreateDTO.getPassword()));
        newEmployee.setEnabled(false);
        Employee savedEmployee = employeeRepository.save(newEmployee);

        limitEmailVerificationRequests.limitEmailVerificationRequested(newEmployee);

        applicationEventPublisher.publishEvent(new CreateRegistrationEvent(this, savedEmployee.getEmail(), savedEmployee));

        return EmployeeMapper.toDTO(savedEmployee);
    }

    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeUpdateDTO employeeUpdateDTO) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found" + id));
        if (employeeRepository.existsByEmailAndIdNot(employeeUpdateDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException
                    ("An employee with this email already exists" + employeeUpdateDTO.getEmail());
        }

        Department dept = departmentRepository.findById(Integer.valueOf(employeeUpdateDTO.getDepartment_id()))
                .orElseThrow(() -> new RuntimeException("Department not found"));
        employee.updateProfile(
                employeeUpdateDTO.getName(),
                employee.getAddress(),
                LocalDate.parse(employeeUpdateDTO.getDateOfBirth()),
                employeeUpdateDTO.getEmail(),
                dept
        );

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDTO(updatedEmployee);

    }


    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeResponseDTO getUserById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return EmployeeMapper.toDTO(employee);

    }
    final Map<String, PatchHandler> handlers;
    public EmployeeResponseDTO patchEmployee(UUID id, EmployeePatchDTO updates) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + id));
        if(updates.getName() != null)
            employee.setName((updates.getName()));

        if(updates.getEmail() != null)
            employee.setEmail(updates.getEmail());

        if(updates.getAddress() != null)
            employee.setAddress(updates.getAddress());

        if(updates.getPassword() != null)
            employee.setPassword(passwordEncoder.encode(updates.getPassword()));

        if(updates.getDepartment_id() != null) {
            Department department = departmentRepository.findById(Integer.valueOf(updates.getDepartment_id()))
                    .orElseThrow(() -> new DepartmentNotFoundException("Department Not Found"));
            employee.setDepartment(department);
        }

        if(updates.getDateOfBirth() != null)
            employee.setDate_of_birth(LocalDate.parse(updates.getDateOfBirth()));

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDTO(updatedEmployee);
    }
    public void updateRole(UUID id, String newRole){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

        if(!isValidRole(newRole))
            throw new InvalidRoleException("This is an invalid role" + newRole);

        employee.changeRole(Role.valueOf(newRole));
        employeeRepository.save(employee);

    }
    private boolean isValidRole(String role){
        return "ROLE_ADMIN".equals(role) || "ROLE_EMPLOYEE".equals(role);
    }
}
