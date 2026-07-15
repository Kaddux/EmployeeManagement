package com.pm.employeeservice.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import com.pm.employeeservice.service.DepartmentService;
import com.pm.employeeservice.util.JwtUtil;
import com.pm.employeeservice.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerTests {

    @Autowired MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean DepartmentService departmentService;
    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test
    void createDepartment_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/department")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void createDepartment_success_returns200() throws Exception {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setDepartment_id(1);
        dto.setDepartment_name("Engineering");
        dto.setDepartment_code("ENG");
        when(departmentService.createDepartment(any())).thenReturn(dto);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("admin@test.com");
        when(customUserDetailsService.loadUserByUsername("admin@test.com"))
                .thenReturn(new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        mockMvc.perform(post("/department")
                       .header("Authorization", "Bearer any-valid-token")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"department_name\":\"Engineering\",\"department_code\":\"ENG\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.department_id").value(1));
    }

    @Test
    void createDepartment_withEmployeeRole_returns403() throws Exception {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("emp@test.com");
        when(customUserDetailsService.loadUserByUsername("emp@test.com"))
                .thenReturn(new User("emp", "", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));

        mockMvc.perform(post("/department")
                       .header("Authorization", "Bearer any-valid-token")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    void deleteDepartment_success_returns204() throws Exception {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("admin@test.com");
        when(customUserDetailsService.loadUserByUsername("admin@test.com"))
                .thenReturn(new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        mockMvc.perform(delete("/department/{id}", 1)
                       .header("Authorization", "Bearer any-valid-token"))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteDepartment_withEmployeeRole_returns403() throws Exception {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractEmail(anyString())).thenReturn("emp@test.com");
        when(customUserDetailsService.loadUserByUsername("emp@test.com"))
                .thenReturn(new User("emp", "", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));

        mockMvc.perform(delete("/department/{id}", 1)
                       .header("Authorization", "Bearer any-valid-token"))
               .andExpect(status().isForbidden());
    }
}
