package com.pm.employeeservice.ModelTests;

import com.pm.employeeservice.model.EmailFailureLog;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailFailureLogTests {

    @Test
    void setErrorMessage_truncatesTo255Chars() {
        EmailFailureLog log = new EmailFailureLog();
        String longMsg = "a".repeat(300);
        log.setErrorMessage(longMsg);
        assertEquals(255, log.getErrorMessage().length());
    }

    @Test
    void setErrorMessage_shortMessageNotTruncated() {
        EmailFailureLog log = new EmailFailureLog();
        log.setErrorMessage("short error");
        assertEquals("short error", log.getErrorMessage());
    }

    @Test
    void setErrorMessage_null_isNull() {
        EmailFailureLog log = new EmailFailureLog();
        log.setErrorMessage(null);
        assertNull(log.getErrorMessage());
    }
}
