package com.pm.employeeservice.ControllerTests;

import com.pm.employeeservice.config.SecurityConfig;
import com.pm.employeeservice.controller.FamilyController;
import com.pm.employeeservice.dto.FamilyResponseDTO;
import com.pm.employeeservice.service.FamilyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(FamilyController.class)
@Import(SecurityConfig.class)
class FamilyControllerTests {

    @Autowired MockMvc mockMvc;
    @MockitoBean
    FamilyService familyService;
    @MockitoBean com.pm.employeeservice.filter.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean com.pm.employeeservice.config.JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void createFamily_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFamily_returns200() throws Exception {
        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setFamilyId(UUID.randomUUID());
        dto.setFather_name("Bob");
        when(familyService.createFamily(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                           {"father_name":"Bob","mother_name":"Alice","number_of_members":4}
                           """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.father_name").value("Bob"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createFamily_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFamily_returns200() throws Exception {
        when(familyService.getFamily()).thenReturn(List.of(new FamilyResponseDTO()));
        mockMvc.perform(get("/family"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUniqueFamily_returns200() throws Exception {
        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setFather_name("Bob");
        when(familyService.getUniqueFamily(any())).thenReturn(dto);

        mockMvc.perform(get("/family/{id}", UUID.randomUUID()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.father_name").value("Bob"));
    }
}
