package controller;


import dto.EmployeeRequestDTO;
import dto.EmployeeResponseDTO;
import model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.EmployeeRepository;
import service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployee(){
        List<EmployeeResponseDTO> employee = employeeService.getUsers();
        return ResponseEntity.ok().body(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @RequestBody EmployeeRequestDTO employeeRequestDTO){
        EmployeeResponseDTO employeeResponseDTO = employeeService.createUser(employeeRequestDTO);

        return ResponseEntity.ok().body(employeeResponseDTO);
    }

    @DeleteMapping
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@PathVariable UUID id){
        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }


}
