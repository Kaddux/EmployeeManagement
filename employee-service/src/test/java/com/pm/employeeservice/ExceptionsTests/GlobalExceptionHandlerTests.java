package com.pm.employeeservice.ExceptionsTests;

import com.pm.employeeservice.Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleEmailAlreadyExistsException_returns409() {
        var ex = new EmailAlreadyExistsException("Email taken");
        ResponseEntity<ApiErrorResponse> resp = handler.handleEmailAlreadyExistsException(ex);
        assertEquals(409, resp.getStatusCode().value());
        assertEquals("Resource Conflict", resp.getBody().getError());
        assertEquals("Email taken", resp.getBody().getMessage());
    }

    @Test
    void handleEmailAlreadyExistsException_withNullMessage_returnsDefault() {
        var ex = new EmailAlreadyExistsException(null);
        ResponseEntity<ApiErrorResponse> resp = handler.handleEmailAlreadyExistsException(ex);
        assertEquals("Email already exists", resp.getBody().getMessage());
    }

    @Test
    void handleEmployeeNotFoundException_returns404() {
        var ex = new EmployeeNotFoundException("Not found");
        ResponseEntity<ApiErrorResponse> resp = handler.handleEmployeeNotFoundException(ex);
        assertEquals(404, resp.getStatusCode().value());
        assertEquals("Not found", resp.getBody().getMessage());
    }

    @Test
    void handleEmployeeNotFoundException_withNullMessage_returnsDefault() {
        var ex = new EmployeeNotFoundException(null);
        ResponseEntity<ApiErrorResponse> resp = handler.handleEmployeeNotFoundException(ex);
        assertEquals("Employee not found", resp.getBody().getMessage());
    }

    @Test
    void handleDepartmentNotFoundException_returns404() {
        var ex = new DepartmentNotFoundException("Dept not found");
        ResponseEntity<ApiErrorResponse> resp = handler.handleDepartmentNotFoundException(ex);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    void handleDepartmentAlreadyExistsException_returns409() {
        var ex = new DepartmentAlreadyExistsException("Dept exists");
        ResponseEntity<ApiErrorResponse> resp = handler.handleDepartmentAlreadyExistsException(ex);
        assertEquals(409, resp.getStatusCode().value());
    }

    @Test
    void handleInvalidRoleException_returns400() {
        var ex = new InvalidRoleException("Bad role");
        ResponseEntity<ApiErrorResponse> resp = handler.handleInvalidRoleException(ex);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void handleUnauthorizedAccessException_returns403() {
        var ex = new UnauthorizedAccessException("No permission");
        ResponseEntity<ApiErrorResponse> resp = handler.handleUnauthorizedAccessException(ex);
        assertEquals(403, resp.getStatusCode().value());
    }

    @Test
    void handleEmailRequestsException_returns400() {
        var ex = new EmailRequestsException("Too fast");
        ResponseEntity<ApiErrorResponse> resp = handler.handleEmailRequestsException(ex);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void handlePagesExhaustedException_returns400() {
        var ex = new PagesExhaustedException("No more pages");
        ResponseEntity<ApiErrorResponse> resp = handler.handlePagesExhaustedException(ex);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void handleGenericException_returns500() {
        var ex = new RuntimeException("Unexpected");
        ResponseEntity<ApiErrorResponse> resp = handler.handleAllExceptions(ex);
        assertEquals(500, resp.getStatusCode().value());
    }

    @Test
    void handleGenericException_withNullMessage_returnsDefault() {
        var ex = new RuntimeException();
        ResponseEntity<ApiErrorResponse> resp = handler.handleAllExceptions(ex);
        assertEquals("Unexpected Error Occurred", resp.getBody().getMessage());
    }

    @Test
    void handleValidationException_returns400() {
        var ex = new org.springframework.web.bind.MethodArgumentNotValidException(null,
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "name"));
        ResponseEntity<ApiErrorResponse> resp = handler.handleValidationException(ex);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void handleHttpMessageNotReadableException_returns400() {
        var ex = new org.springframework.http.converter.HttpMessageNotReadableException("Malformed", org.mockito.Mockito.mock(org.springframework.http.HttpInputMessage.class));
        ResponseEntity<ApiErrorResponse> resp = handler.handleHttpMessageNotReadableException(ex);
        assertEquals(400, resp.getStatusCode().value());
    }
}
