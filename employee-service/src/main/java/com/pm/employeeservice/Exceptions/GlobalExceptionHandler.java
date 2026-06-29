package com.pm.employeeservice.Exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){

        log.warn("Email already exists {}", ex.getMessage());

        Map<String,String> errors= new HashMap<>();
        errors.put("message", "email already exists");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleEmployeeNotFoundException(EmployeeNotFoundException ex){
        log.warn("Employee not found {}", ex.getMessage());

        Map<String,String> errors = new HashMap<>();
        errors.put("message","Employee not found");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleDepartmentAlreadyExistsException(DepartmentAlreadyExistsException ex){
        log.warn("Department Already exists");

        Map<String,String> error = new HashMap<>();
        error.put("message","This Department Already Exists");

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleDepartmentNotFoundException(DepartmentNotFoundException ex) {
        log.warn("Department Not Found");

        Map<String, String> error = new HashMap<>();

        error.put("message", "Department Not found");

        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String,String>> handleInvalidRoleException(InvalidRoleException ex){
        log.warn("Invalid Role");

        Map<String,String> error = new HashMap<>();
        error.put("message","This is an invalid role");

        return ResponseEntity.badRequest().body(error);
    }
}
