package controller;


import dto.DepartmentRequestDTO;
import dto.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.DepartmentService;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDTO> postDepartment(@RequestBody DepartmentRequestDTO departmentRequestDTO){

        DepartmentResponseDTO departmentResponseDTO = departmentService.createDepartment(departmentRequestDTO);


        return ResponseEntity.ok().body(departmentResponseDTO);
    }

    @DeleteMapping("/{department_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDTO> deleteDepartment(@PathVariable Integer department_id){
         departmentService.deleteDepartment(department_id);

        return ResponseEntity.noContent().build();
    }
}
