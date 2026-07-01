package com.pm.employeeservice.Exceptions;

public class PagesExhaustedException extends RuntimeException {
    public PagesExhaustedException(String message) {
        super(message);
    }
}
