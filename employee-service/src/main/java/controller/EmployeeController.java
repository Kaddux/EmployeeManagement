package controller;


import Exceptions.DepartmentNotFoundException;
import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import mapper.EmployeeMapper;
import model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repository.EmployeeRepository;
import repository.DepartmentRepository;
import model.Department;
import service.EmployeeService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static Enum.Role.ROLE_ADMIN;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    //---------------------------------------------------TO-DO----------------------------------------------------------
    //-> ADMIN HOD's (head of a particular department) -> return details of all employee from his department
    //
    //-> employee can access only their data
    //
    //-> ADMIN can access all details

    //SEND EMAIL TO NEW USERS
    //---------------------------------------------------TO-DO----------------------------------------------------------
    //GET method for both EMPLOYEE and ADMIN role
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployee(){
        List<EmployeeResponseDTO> employee = employeeService.getUsers();
        return ResponseEntity.ok().body(employee);
    }

    //POST method configured for only ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @RequestBody EmployeeRequestDTO employeeRequestDTO){
        EmployeeResponseDTO employeeResponseDTO = employeeService.createUser(employeeRequestDTO);

        return ResponseEntity.ok().body(employeeResponseDTO);
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
                                                     @RequestBody EmployeeRequestDTO updates) {
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
                case "password": emp.setPassword((String) value); break;
                case "role": emp.setRole(ROLE_ADMIN); break;
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

}
