package com.pm.employeeservice.mail;

import com.pm.employeeservice.model.Employee;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class CreateActivationEvent extends ApplicationEvent
{
    private final String email;
    private final Employee employee;

    public CreateActivationEvent(Object source, String email, Employee employee){
        super(source);
        this.email=email;
        this.employee = employee;
    }

}
