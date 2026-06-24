package service;


import Exceptions.EmailAlreadyExistsException;
import Exceptions.EmployeeNotFoundException;
import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import mail.CreateRegistrationEvent;
import mapper.EmployeeMapper;
import model.Employee;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import repository.EmployeeRepository;
import repository.DepartmentRepository;
import model.Department;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

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

    public List<EmployeeResponseDTO> getUsers(){
       List<Employee> employees =  employeeRepository.findAll();
       return employees.stream().map(EmployeeMapper::toDTO).toList();
    }

    public EmployeeResponseDTO createUser(EmployeeRequestDTO employeeRequestDTO){
        if(employeeRepository.existsByEmail(employeeRequestDTO.getEmail()))
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
    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO employeeRequestDTO){
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found"+ id));
        if(employeeRepository.existsByEmailAndIdNot(employeeRequestDTO.getEmail(), id)){
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
    public void deleteEmployee(UUID id){
        employeeRepository.deleteById(id);
    }

    public EmployeeResponseDTO getUserById(UUID id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return EmployeeMapper.toDTO(employee);

    }
}
