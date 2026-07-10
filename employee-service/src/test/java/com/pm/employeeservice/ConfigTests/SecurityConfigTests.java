package com.pm.employeeservice.ConfigTests;

import com.pm.employeeservice.config.JwtAuthenticationEntryPoint;
import com.pm.employeeservice.config.SecurityConfig;
import com.pm.employeeservice.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(controllers = DummyController.class)
@Import(SecurityConfig.class)
class SecurityConfigTests {

    @Autowired MockMvc mockMvc;
    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void securedEndpoint_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/family"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void publicEndpoint_isAccessible() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                       .contentType("application/json")
                       .content("{\"email\":\"a@b.com\",\"password\":\"p\"}"))
               .andExpect(status().isUnauthorized());
    }
}
