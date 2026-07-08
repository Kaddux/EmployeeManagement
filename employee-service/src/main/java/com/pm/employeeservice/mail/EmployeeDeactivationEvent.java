package com.pm.employeeservice.mail;

import com.pm.employeeservice.dto.ExpiredTokenProjection;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmployeeDeactivationEvent extends ApplicationEvent {
    private final ExpiredTokenProjection verificationToken;
    private final String newToken;

    public EmployeeDeactivationEvent(Object source, ExpiredTokenProjection verificationToken, String newToken){
        super(source);
        this.verificationToken = verificationToken;
        this.newToken = newToken;
    }
}
