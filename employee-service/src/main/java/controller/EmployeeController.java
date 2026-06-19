package controller;


import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repository.EmployeeRepository;
import service.EmployeeService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

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
                                                     @RequestBody Map<String, Object> updates) {
        EmployeeResponseDTO userResponseDTO = employeeService.updateEmployee(id, (EmployeeRequestDTO) updates);
        return ResponseEntity.ok().body(userResponseDTO);
    }

}
