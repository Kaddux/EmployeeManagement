package com.pm.employeeservice.service;


import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.Exceptions.EmailAlreadyExistsException;
import com.pm.employeeservice.Exceptions.EmployeeNotFoundException;
import com.pm.employeeservice.Exceptions.InvalidRoleException;
import com.pm.employeeservice.dto.AdminOnlyDTO;
import com.pm.employeeservice.dto.EmployeeRequestDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.mail.CreateRegistrationEvent;
import com.pm.employeeservice.mapper.EmployeeMapper;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.repository.EmployeeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public EmployeeService(ApplicationEventPublisher applicationEventPublisher, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<EmployeeResponseDTO> getUsers() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(EmployeeMapper::toDTO).toList();
    }

    public EmployeeResponseDTO createUser(EmployeeRequestDTO employeeRequestDTO) {
        if (employeeRepository.existsByEmail(employeeRequestDTO.getEmail()))
            throw new EmailAlreadyExistsException("User with this email already exists" + employeeRequestDTO.getEmail());

        Employee newEmployee = EmployeeMapper.toModel(employeeRequestDTO);

        Department dept = departmentRepository.findById(Integer.valueOf(employeeRequestDTO.getDepartment_id()))
                .orElseThrow(() -> new RuntimeException("Department not found"));
        newEmployee.setDepartment(dept);

        newEmployee.setPassword(passwordEncoder.encode(employeeRequestDTO.getPassword()));
        newEmployee.setEnabled(false);
        Employee savedEmployee = employeeRepository.save(newEmployee);

        applicationEventPublisher.publishEvent(new CreateRegistrationEvent(this, savedEmployee.getEmail(), savedEmployee));

        return EmployeeMapper.toDTO(savedEmployee);
    }

    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found" + id));
        if (employeeRepository.existsByEmailAndIdNot(employeeRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException
                    ("An employee with this email already exists" + employeeRequestDTO.getEmail());
        }
        employee.setName(employeeRequestDTO.getName());
        employee.setAddress(employeeRequestDTO.getAddress());
        employee.setRole(employee.getRole());
        employee.setPassword(employee.getPassword());

        Department dept = departmentRepository.findById(Integer.valueOf(employeeRequestDTO.getDepartment_id()))
                .orElseThrow(() -> new RuntimeException("Department not found"));
        employee.setDepartment(dept);
        employee.setDate_of_birth(LocalDate.parse(employeeRequestDTO.getDateOfBirth()));
        employee.setEmail(employeeRequestDTO.getEmail());

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
    public void updateRole(UUID id, String newRole){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

        if(!isValidRole(newRole))
            throw new InvalidRoleException("This is an invalid role" + newRole);

        employee.setRole(Role.valueOf(newRole));
        employeeRepository.save(employee);

    }
    private boolean isValidRole(String role){
        return "ROLE_ADMIN".equals(role) || "ROLE_EMPLOYEE".equals(role);
    }
}
