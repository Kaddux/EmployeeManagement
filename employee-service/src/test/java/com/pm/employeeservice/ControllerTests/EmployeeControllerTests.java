package com.pm.employeeservice.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.employeeservice.config.SecurityConfig;
import com.pm.employeeservice.controller.EmployeeController;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.Excel.EmployeeExcelExporter;
import com.pm.employeeservice.Excel.EmployeeExcelImporter;
import com.pm.employeeservice.service.CustomUserDetailsService;
import com.pm.employeeservice.service.EmployeeService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTests {

    @Autowired MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean EmployeeService employeeService;
    @MockitoBean EmployeeExcelExporter employeeExportEngine;
    @MockitoBean EmployeeExcelImporter employeeImportEngine;
    // Mock JwtUtil & CustomUserDetailsService so the real JwtAuthenticationFilter can run
    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test
    void getEmployees_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/employees"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getEmployees_withUserRole_returns200() throws Exception {
        Page<EmployeeResponseDTO> page = new PageImpl<>(List.of());
        when(employeeService.getUsers(0, 10)).thenReturn(page);
        mockMvc.perform(get("/employees"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEmployees_withCustomPagination() throws Exception {
        when(employeeService.getUsers(2, 5)).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/employees")
                       .param("page", "2")
                       .param("size", "5"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getEmployeeById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        dto.setName("John");
        when(employeeService.getUserById(id)).thenReturn(dto);
        mockMvc.perform(get("/employees/{id}", id))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/employees")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_validBody_returns201() throws Exception {
        EmployeeCreateDTO request = new EmployeeCreateDTO();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("pass123");
        request.setRole("ROLE_EMPLOYEE");
        request.setDepartment_id("1");
        request.setAddress("123 St");
        request.setDateOfBirth("1990-01-01");

        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(UUID.randomUUID().toString());
        response.setName("John");

        when(employeeService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/employees")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/employees")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"john@example.com\",\"password\":\"pass\",\"role\":\"ROLE_EMPLOYEE\"," +
                                "\"department_id\":\"1\",\"address\":\"St\",\"dateOfBirth\":\"1990-01-01\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        when(employeeService.updateEmployee(any(), any())).thenReturn(dto);
        mockMvc.perform(put("/employees/{id}", id)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"name\":\"Updated\",\"email\":\"u@e.com\",\"role\":\"ROLE_ADMIN\"," +
                                "\"department_id\":\"1\",\"address\":\"A\",\"dateOfBirth\":\"1990-01-01\"}"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(put("/employees/{id}", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        when(employeeService.patchEmployee(any(), any())).thenReturn(dto);
        mockMvc.perform(patch("/employees/{id}", id)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"name\":\"Patched\"}"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_returns204() throws Exception {
        mockMvc.perform(delete("/employees/{id}", UUID.randomUUID()))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(delete("/employees/{id}", UUID.randomUUID()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resendActivationEmail_returns200() throws Exception {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        when(employeeService.resendActivationMail("user@example.com")).thenReturn(dto);
        mockMvc.perform(post("/employees/resend-activation")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"user@example.com\"}"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRole_returns200() throws Exception {
        mockMvc.perform(patch("/employees/{id}/role", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"role\":\"ROLE_ADMIN\"}"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importEmployees_emptyFile_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
        mockMvc.perform(multipart("/employees/import").file(file))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importEmployees_wrongContentType_returns415() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "some data".getBytes());
        mockMvc.perform(multipart("/employees/import").file(file))
               .andExpect(status().isUnsupportedMediaType());
    }
}
