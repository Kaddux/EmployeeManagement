package com.pm.employeeservice.mail;

import lombok.Getter;
import com.pm.employeeservice.model.Employee;
import org.springframework.context.ApplicationEvent;


@Getter
public class CreateRegistrationEvent extends ApplicationEvent {
    private final String email;
    private final Employee employee;


    public CreateRegistrationEvent(Object source, String email, Employee employee) {
        super(source);
        this.email = email;
        this.employee = employee;

    }

}
