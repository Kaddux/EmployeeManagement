package com.pm.employeeservice.mail;

import com.pm.employeeservice.dto.NewEmployeeInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class ExcelImportEvent extends ApplicationEvent {
    private final List<NewEmployeeInfo> employeeInfo;
    private final UUID jobId;

    public ExcelImportEvent(Object source, List<NewEmployeeInfo> employeeInfo, UUID jobId) {
        super(source);
        this.employeeInfo = employeeInfo;
        this.jobId = jobId;
    }
}
