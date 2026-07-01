package com.pm.employeeservice.Exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){

        log.warn("Email already exists {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                409,
                "Resource Conflict",
                "Email already exists"
        );
        return ResponseEntity.badRequest().body(response);

    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex){

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                404,
                "Resource Not Found",
                "Employee not found"
        );

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleDepartmentAlreadyExistsException(DepartmentAlreadyExistsException ex){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                409,
                "Resource Conflict",
                "Department Already exists"
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }


    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDepartmentNotFoundException(DepartmentNotFoundException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                404,
                "Resource Not Found",
                "Department not found"
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRoleException(InvalidRoleException ex){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                400,
                "Bad Request",
                "Invalid Role"
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
    @ExceptionHandler(PagesExhaustedException.class)
    public ResponseEntity<ApiErrorResponse> handlePagesExhaustedException(PagesExhaustedException ex){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                400,
                "Bad Request",
                "That Page does not exist."
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex){

        ApiErrorResponse response = new ApiErrorResponse(
                403,
                "Access denied",
                "You do not have the permission to access this resource."
        );

        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                400,
                "Bad Request",
                "Invalid Fields",
                errors,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Bad Request",
                "Malformed JSON or Invalid request body"
        );
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex){
        ApiErrorResponse response = new ApiErrorResponse(
                500,
                "Internal Server Error",
                "Unexpected Error Occurred"
        );
        return ResponseEntity.badRequest().body(response);
    }

}
