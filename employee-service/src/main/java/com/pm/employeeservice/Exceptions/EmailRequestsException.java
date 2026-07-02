package com.pm.employeeservice.Exceptions;

public class EmailRequestsException extends RuntimeException {
    public EmailRequestsException(String message) {
        super(message);
    }
}
