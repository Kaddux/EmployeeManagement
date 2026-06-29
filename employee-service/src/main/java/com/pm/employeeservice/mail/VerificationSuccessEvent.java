package com.pm.employeeservice.mail;

import lombok.Getter;
import com.pm.employeeservice.model.Employee;
import org.springframework.context.ApplicationEvent;

@Getter
public class VerificationSuccessEvent extends ApplicationEvent {
    private final Employee employee;

    public VerificationSuccessEvent(Object source, Employee employee){
        super(source);
        this.employee = employee;
    }
}
