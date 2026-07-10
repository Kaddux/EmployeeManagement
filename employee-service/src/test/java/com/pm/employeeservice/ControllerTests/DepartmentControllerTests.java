package com.pm.employeeservice.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.employeeservice.config.SecurityConfig;
import com.pm.employeeservice.controller.DepartmentController;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import com.pm.employeeservice.service.CustomUserDetailsService;
import com.pm.employeeservice.service.DepartmentService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(DepartmentController.class)
@Import(SecurityConfig.class)
class DepartmentControllerTests {

    @Autowired MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean DepartmentService departmentService;
    // Mock JwtUtil & CustomUserDetailsService so the real JwtAuthenticationFilter can run
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
    @WithMockUser(roles = "ADMIN")
    void createDepartment_success_returns200() throws Exception {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setDepartment_id(1);
        dto.setDepartment_name("Engineering");
        dto.setDepartment_code("ENG");
        when(departmentService.createDepartment(any())).thenReturn(dto);

        mockMvc.perform(post("/department")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"department_name\":\"Engineering\",\"department_code\":\"ENG\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.department_id").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createDepartment_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/department")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDepartment_success_returns204() throws Exception {
        mockMvc.perform(delete("/department/{id}", 1))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteDepartment_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(delete("/department/{id}", 1))
               .andExpect(status().isForbidden());
    }
}
