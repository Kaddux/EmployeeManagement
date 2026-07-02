package com.pm.employeeservice.controller;


import com.pm.employeeservice.Exceptions.DepartmentNotFoundException;
import com.pm.employeeservice.dto.AdminOnlyDTO;
import com.pm.employeeservice.dto.EmployeeRequestDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.mapper.EmployeeMapper;
import com.pm.employeeservice.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.service.EmployeeService;

import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(PasswordEncoder passwordEncoder,EmployeeService employeeService, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //---------------------------------------------------TO-DO----------------------------------------------------------
    //-> ADMIN HOD's (head of a particular department) -> return details of all employee from his department
    //
    //-> employee can access only their data
    //
    //-> ADMIN can access all details

    //SEND EMAIL TO NEW USERS
    //---------------------------------------------------TO-DO----------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<com.pm.employeeservice.dto.EmployeePageResponseDTO<EmployeeResponseDTO>> getEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        com.pm.employeeservice.dto.EmployeePageResponseDTO<EmployeeResponseDTO> employeePage =
                employeeService.getUsers(page, size);

        return ResponseEntity.ok().body(employeePage);
    }

    //POST method configured for only ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO){
        EmployeeResponseDTO employeeResponseDTO = employeeService.createUser(employeeRequestDTO);

        return ResponseEntity.ok().body(employeeResponseDTO);
    }
    @PostMapping("/resend-activation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> resendActivationEmail(@RequestBody Map<String, String> payload){
        String email = payload.get("email");
        EmployeeResponseDTO activationResponse = employeeService.resendActivationMail(email);

        return ResponseEntity.ok().body(activationResponse);
    }

    //DELETE method configured for only ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@PathVariable UUID id){
        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }

    //PUT method configured for only ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> putUser(@PathVariable UUID id,
                                                     @Valid @RequestBody EmployeeRequestDTO updates) {
        EmployeeResponseDTO userResponseDTO = employeeService.updateEmployee(id, updates);
        return ResponseEntity.ok().body(userResponseDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponseDTO patchUser(@PathVariable UUID id, @RequestBody Map<String,Object> updates){

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        updates.forEach((key,value)->{
            switch (key){
                case "email": emp.setEmail((String) value); break;
                case "name": emp.setName((String) value); break;
                case "password":{
                    String password = passwordEncoder.encode((String) value);
                    emp.setPassword(password); break;
                }
                case "department_id":
                    assert value instanceof Integer;
                    Department dept = departmentRepository.findById((Integer) value)
                            .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
                    emp.setDepartment(dept);
                    break;
                default: throw new IllegalArgumentException("Invalid field" + key);
            }
        });
        Employee updatedEmployee = employeeRepository.save(emp);
        return EmployeeMapper.toDTO(updatedEmployee);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> getUniqueUser(@PathVariable UUID id){
        EmployeeResponseDTO employee = employeeService.getUserById(id);

        return ResponseEntity.ok().body(employee);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @RequestBody AdminOnlyDTO adminOnlyDTO){
        employeeService.updateRole(id, adminOnlyDTO.getRole());

        log.info("Employee role has successfully been updated");

        return ResponseEntity.ok().build();
    }

}
