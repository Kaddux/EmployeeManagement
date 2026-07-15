package com.pm.employeeservice.ControllerTests;

import com.pm.employeeservice.dto.FamilyResponseDTO;
import com.pm.employeeservice.service.CustomUserDetailsService;
import com.pm.employeeservice.service.FamilyService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FamilyControllerTests {

    @Autowired MockMvc mockMvc;
    @MockitoBean FamilyService familyService;
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
    void createFamily_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void createFamily_returns200() throws Exception {
        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setFamilyId(UUID.randomUUID());
        dto.setFather_name("Bob");
        when(familyService.createFamily(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .header("Authorization", ADMIN_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                           {"father_name":"Bob","mother_name":"Alice","number_of_members":4}
                           """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.father_name").value("Bob"));
    }

    @Test
    void createFamily_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/family/{employeeId}", UUID.randomUUID())
                       .header("Authorization", EMP_TOKEN)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{}"))
               .andExpect(status().isForbidden());
    }

    @Test
    void getFamily_returns200() throws Exception {
        when(familyService.getFamily()).thenReturn(List.of(new FamilyResponseDTO()));
        mockMvc.perform(get("/family")
                       .header("Authorization", ADMIN_TOKEN))
               .andExpect(status().isOk());
    }

    @Test
    void getUniqueFamily_returns200() throws Exception {
        FamilyResponseDTO dto = new FamilyResponseDTO();
        dto.setFather_name("Bob");
        when(familyService.getUniqueFamily(any())).thenReturn(dto);

        mockMvc.perform(get("/family/{id}", UUID.randomUUID())
                       .header("Authorization", ADMIN_TOKEN))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.father_name").value("Bob"));
    }
}