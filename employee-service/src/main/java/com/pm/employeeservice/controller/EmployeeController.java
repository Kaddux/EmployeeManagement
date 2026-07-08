package com.pm.employeeservice.controller;


import com.pm.employeeservice.Excel.EmployeeExcelExporter;
import com.pm.employeeservice.Excel.EmployeeExcelImporter;
import com.pm.employeeservice.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.pm.employeeservice.service.EmployeeService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeExcelExporter employeeExportEngine;
    private final EmployeeExcelImporter employeeImportEngine;

    public EmployeeController(EmployeeExcelImporter employeeImportEngine,
                              EmployeeExcelExporter employeeExportEngine,EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.employeeExportEngine = employeeExportEngine;
        this.employeeImportEngine = employeeImportEngine;
    }

    //---------------------------------------------------TO-DO----------------------------------------------------------
    //-> Export Employee Data to an Excel file
    //-> Import Excel Data from an Excel file into Database
    //-> Send a sort of "Welcome" PDF with directions for a new Employee registration
    //---------------------------------------------------TO-DO----------------------------------------------------------

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportToExcel(HttpServletResponse response){
        // 1. Establish strict headers to prevent browser caching and define attachment behavior
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees_export_" + System.currentTimeMillis() + ".xlsx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        try{
            employeeExportEngine.streamExport(response.getOutputStream());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Streaming export failed due to client disconnect or IO Exception");
        }
    }
    @PostMapping(value = "import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String,Object>> importEmployees
            (@RequestParam("file") MultipartFile file){
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","Body cannot be empty"));
        }
        String contentType = file.getContentType();

        if(contentType == null || !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("error","Invalid file extension. Only valid OOXML .xlsx files allowed"));
        }
        UUID jobId = UUID.randomUUID();
        try {
            employeeImportEngine.streamImport(file.getInputStream(), jobId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("jobId", jobId,
                "status", "PROCESSING",
                "message", "File execution loop initialized successfully."));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Page<EmployeeResponseDTO>> getEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws ResponseStatusException {
        Page<EmployeeResponseDTO> pageable= employeeService.getUsers(page,size);

        return ResponseEntity.ok().body(pageable);
    }

    //POST method configured for only ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeCreateDTO employeeCreateDTO){

        if (employeeCreateDTO.getName() != null) {
            employeeCreateDTO.setName(org.springframework.web.util.HtmlUtils.htmlEscape(employeeCreateDTO.getName()));
        }
        if (employeeCreateDTO.getAddress() != null) {
            employeeCreateDTO.setAddress(org.springframework.web.util.HtmlUtils.htmlEscape(employeeCreateDTO.getAddress()));
        }
        EmployeeResponseDTO created = employeeService.createUser(employeeCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()                    // /api/v1/employees
                .path("/{id}")                           // /{id}
                .buildAndExpand(created.getId())         // substitute actual UUID
                .toUri();


         // SECURITY: Response is JSON with X-Content-Type-Options: nosniff; 
        // user input HTML-escaped in serialization. False positive for XSS.
        return ResponseEntity.created(location).body(created);
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
                                                     @Valid @RequestBody EmployeeUpdateDTO updates) {
        EmployeeResponseDTO userResponseDTO = employeeService.updateEmployee(id, updates);
        return ResponseEntity.ok().body(userResponseDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> patchUser(@PathVariable UUID id, @Valid @RequestBody EmployeePatchDTO updates) {
        EmployeeResponseDTO response = employeeService.patchEmployee(id, updates);

        return ResponseEntity.ok().body(response);
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
