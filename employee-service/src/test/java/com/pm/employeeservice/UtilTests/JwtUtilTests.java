package com.pm.employeeservice.UtilTests;

import com.pm.employeeservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTests {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("dGhpcyBpcyBhIHNhbXBsZSBzZWNyZXQga2V5IGZvciBkZXZlbG9wbWVudCEhIQ==");
    }

    @Test
    void generateToken_returnsNonNull() {
        String token = jwtUtil.generateToken("user@example.com", "ROLE_ADMIN");
        assertNotNull(token);
    }

    @Test
    void validateToken_ownToken_returnsTrue() {
        String token = jwtUtil.generateToken("user@example.com", "ROLE_ADMIN");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void extractEmail_returnsSubject() {
        String token = jwtUtil.generateToken("user@example.com", "ROLE_ADMIN");
        assertEquals("user@example.com", jwtUtil.extractEmail(token));
    }

    @Test
    void validateToken_tamperedToken_returnsFalse() {
        String token = jwtUtil.generateToken("user@example.com", "ROLE_ADMIN");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void validateToken_garbageString_returnsFalse() {
        assertFalse(jwtUtil.validateToken("MeowMeowMeowMeow"));
    }

    @Test
    void validateToken_emptyString_returnsFalse() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void constructorWithRawSecret_doesNotThrow() {
        assertDoesNotThrow(() -> new JwtUtil("not-base64-encoded-secret-key-here"));
    }
}
