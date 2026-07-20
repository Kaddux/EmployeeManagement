package com.pm.employeeservice.ModelTests;

import com.pm.employeeservice.model.VerificationToken;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerificationTests {

    @Test
    void isExpired_whenPast_ExpiryDate_returnsTrue(){
        VerificationToken token = new VerificationToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        assertTrue(token.isExpired());
    }
    @Test
    void isExpired_whenFuture_ExpiryDate_returnsFalse(){
        VerificationToken token = new VerificationToken();
        token.setExpiryDate(LocalDateTime.now().plusDays(1));

        assertFalse(token.isExpired());
    }
}
