package com.pm.employeeservice.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.employeeservice.dto.EmployeeCreateDTO;
import com.pm.employeeservice.dto.EmployeeResponseDTO;
import com.pm.employeeservice.Excel.EmployeeExcelExporter;
import com.pm.employeeservice.Excel.EmployeeExcelImporter;
import com.pm.employeeservice.service.CustomUserDetailsService;
import com.pm.employeeservice.service.EmployeeService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTests {

    @Autowired MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean EmployeeService employeeService;
    @MockitoBean EmployeeExcelExporter employeeExportEngine;
    @MockitoBean EmployeeExcelImporter employeeImportEngine;
    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    private static final String ADMIN_TOKEN = "Bearer admin-token";
    private static final String EMP_TOKEN = "Bearer emp-token";

    @BeforeEach
    void setUpAuth() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenAnswer(inv -> {
            String token = inv.getArgument(0);
            return token.contains("admin") ? "admin@test.com" : "emp@test.com";
        });
        when(customUserDetailsService.loadUserByUsername("admin@test.com"))
                .thenReturn(new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        when(customUserDetailsService.loadUserByUsername("emp@test.com"))
                .thenReturn(new User("emp", "", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));
    }

    @Test
    void getEmployees_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/employees"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getEmployees_withUserRole_returns200() throws Exception {
        Page<EmployeeResponseDTO> page = new PageImpl<>(List.of());
        when(employeeService.getUsers(0, 10)).thenReturn(page);
        mockMvc.perform(get("/employees").header("Authorization", EMP_TOKEN))
               .andExpect(status().isOk());
    }

    @Test
    void getEmployees_withCustomPagination() throws Exception {
        when(employeeService.getUsers(2, 5)).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/employees")
                       .header("Authorization", ADMIN_TOKEN)
                       .param("page", "2")
                       .param("size", "5"))
               .andExpect(status().isOk());
    }

    @Test
    void getEmployeeById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        dto.setName("John");
        when(employeeService.getUserById(id)).thenReturn(dto);
        mockMvc.perform(get("/employees/{id}", id)
                       .header("Authorization", EMP_TOKEN))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void createEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/employees")
                       .header("Authorization", EMP_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
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
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }

    @Test
    void createEmployee_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/employees")
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"john@example.com\",\"password\":\"pass\",\"role\":\"ROLE_EMPLOYEE\"," +
                                "\"department_id\":\"1\",\"address\":\"St\",\"dateOfBirth\":\"1990-01-01\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        when(employeeService.updateEmployee(any(), any())).thenReturn(dto);
        mockMvc.perform(put("/employees/{id}", id)
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"name\":\"Updated\",\"email\":\"u@e.com\",\"role\":\"ROLE_ADMIN\"," +
                                "\"department_id\":\"1\",\"address\":\"A\",\"dateOfBirth\":\"1990-01-01\"}"))
               .andExpect(status().isOk());
    }

    @Test
    void updateEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(put("/employees/{id}", UUID.randomUUID())
                       .header("Authorization", EMP_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    void patchEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id.toString());
        when(employeeService.patchEmployee(any(), any())).thenReturn(dto);
        mockMvc.perform(patch("/employees/{id}", id)
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"name\":\"Patched\"}"))
               .andExpect(status().isOk());
    }

    @Test
    void deleteEmployee_returns204() throws Exception {
        mockMvc.perform(delete("/employees/{id}", UUID.randomUUID())
                       .header("Authorization", ADMIN_TOKEN))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(delete("/employees/{id}", UUID.randomUUID())
                       .header("Authorization", EMP_TOKEN))
               .andExpect(status().isForbidden());
    }

    @Test
    void resendActivationEmail_returns200() throws Exception {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        when(employeeService.resendActivationMail("user@example.com")).thenReturn(dto);
        mockMvc.perform(post("/employees/resend-activation")
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"user@example.com\"}"))
               .andExpect(status().isOk());
    }

    @Test
    void updateRole_returns200() throws Exception {
        mockMvc.perform(patch("/employees/{id}/role", UUID.randomUUID())
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"role\":\"ROLE_ADMIN\"}"))
               .andExpect(status().isOk());
    }

    @Test
    void importEmployees_emptyFile_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
        mockMvc.perform(multipart("/employees/import").file(file)
                       .header("Authorization", ADMIN_TOKEN))
               .andExpect(status().isBadRequest());
    }

    @Test
    void importEmployees_wrongContentType_returns415() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "some data".getBytes());
        mockMvc.perform(multipart("/employees/import").file(file)
                       .header("Authorization", ADMIN_TOKEN))
               .andExpect(status().isUnsupportedMediaType());
    }
}