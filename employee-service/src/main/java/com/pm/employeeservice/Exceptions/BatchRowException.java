package com.pm.employeeservice.Exceptions;

import lombok.Getter;
import lombok.Setter;


public class BatchRowException extends Exception{
    private final String error_code;
    @Getter
    private final String columnName;
    @Getter
    private final String rejectedValue;
    @Setter
    @Getter
    private int rowNumber;

    public BatchRowException(String error_code,
                             String columnName, String rejectedValue, String rowNumber, String message ){
        super(message);
        this.error_code = error_code;
        this.columnName = columnName;
        this.rejectedValue = columnName;
    }
    public BatchRowException(String error_code,
                             String columnName, String rowNumber, String message ){
        super(message);
        this.error_code = error_code;
        this.columnName = columnName;
        this.rejectedValue = columnName;
    }


    public String getErrorCode() { return error_code; }

}
