package mail;

import lombok.Getter;
import model.Employee;
import org.springframework.context.ApplicationEvent;

@Getter
public class VerificationSuccessEvent extends ApplicationEvent {
    private final Employee employee;

    public VerificationSuccessEvent(Object source, Employee employee){
        super(source);
        this.employee = employee;
    }
}
