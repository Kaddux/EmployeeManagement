package com.pm.employeeservice.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.employeeservice.dto.LoginRequestDTO;
import com.pm.employeeservice.dto.SetPasswordRequestDTO;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.VerificationToken;
import com.pm.employeeservice.repository.EmployeeRepository;
import com.pm.employeeservice.repository.VerificationTokenRepository;
import com.pm.employeeservice.service.AuthService;
import com.pm.employeeservice.service.CustomUserDetailsService;
import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean AuthService authService;
    @MockitoBean VerificationTokenRepository verificationTokenRepository;
    @MockitoBean EmployeeRepository employeeRepository;
    @MockitoBean ApplicationEventPublisher applicationEventPublisher;
    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        when(authService.authenticate(any())).thenReturn(Optional.of("jwt-token"));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("pass");

        mockMvc.perform(post("/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.authenticate(any())).thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("bad@example.com");
        dto.setPassword("wrong");

        mockMvc.perform(post("/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingEmail_returns400() throws Exception {
        mockMvc.perform(post("/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"password\":\"pass\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void validate_validToken_returns200() throws Exception {
        when(authService.validateToken("good-token")).thenReturn(true);
        mockMvc.perform(get("/validate")
                       .header("Authorization", "Bearer good-token"))
               .andExpect(status().isOk());
    }

    @Test
    void validate_missingHeader_returns500() throws Exception {
        mockMvc.perform(get("/validate"))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void validate_invalidSchema_returns401() throws Exception {
        mockMvc.perform(get("/validate")
                       .header("Authorization", "Basic token"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void validate_expiredToken_returns401() throws Exception {
        when(authService.validateToken("expired-token")).thenReturn(false);
        mockMvc.perform(get("/validate")
                       .header("Authorization", "Bearer expired-token"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyEmail_validToken_returnsOk() throws Exception {
        Employee emp = new Employee();
        emp.setEnabled(true);
        VerificationToken vt = new VerificationToken();
        vt.setEmployee(emp);
        vt.setExpiryDate(java.time.LocalDateTime.now().plusDays(1));
        when(verificationTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(vt));

        mockMvc.perform(get("/verify").param("token", "valid-token"))
               .andExpect(status().isOk());
    }

    @Test
    void verifyEmail_invalidToken_returns400() throws Exception {
        when(verificationTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());
        mockMvc.perform(get("/verify").param("token", "bad-token"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_expiredToken_returns400() throws Exception {
        Employee emp = new Employee();
        emp.setEnabled(false);
        VerificationToken vt = new VerificationToken();
        vt.setEmployee(emp);
        vt.setExpiryDate(java.time.LocalDateTime.now().minusMinutes(1));
        when(verificationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(vt));

        mockMvc.perform(get("/verify").param("token", "expired-token"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void setPassword_success_returns200() throws Exception {
        when(authService.setPassword("token", "newPass123"))
                .thenReturn("Password has been set successfully.");
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("token");
        dto.setNewPassword("newPass123");
        mockMvc.perform(post("/set-password")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk());
    }

    @Test
    void setPassword_expiredToken_returns400() throws Exception {
        when(authService.setPassword("expired", "pass"))
                .thenThrow(new IllegalArgumentException("Token has expired"));
        SetPasswordRequestDTO dto = new SetPasswordRequestDTO();
        dto.setToken("expired");
        dto.setNewPassword("pass");
        mockMvc.perform(post("/set-password")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isBadRequest());
    }
}