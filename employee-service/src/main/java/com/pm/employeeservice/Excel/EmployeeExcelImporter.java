package com.pm.employeeservice.Excel;

import com.github.pjfanning.xlsx.StreamingReader;
import com.pm.employeeservice.Exceptions.BatchErrorLogger;
import com.pm.employeeservice.Exceptions.BatchRowException;
import com.pm.employeeservice.dto.NewEmployeeInfo;
import com.pm.employeeservice.mail.ExcelImportEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeExcelImporter {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final int BATCH_SIZE = 200;
    private final PasswordEncoder passwordEncoder;
    private final BatchErrorLogger batchErrorLogger;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private ApplicationContext applicationContext;

    public void streamImport(InputStream inputStream, UUID id) throws IOException {
        EmployeeExcelImporter self = applicationContext.getBean(EmployeeExcelImporter.class);
        String sql ="INSERT INTO employee (id, name, email, role, password, address, date_of_birth, department_id, enabled) " +
                "VALUES (:id, :name, :email, :role, :password, :address, :dob, :deptId, :enabled)";

        List<MapSqlParameterSource> batchBuffer = new ArrayList<>(BATCH_SIZE);
        List<BatchRowException> errorList = new ArrayList<>();
        List<NewEmployeeInfo> newlyCreated = new ArrayList<>();
        String defaultHashPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString().replace("-", ""));
        DataFormatter dataFormatter = new DataFormatter();

        try(Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(inputStream)){
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;

            for(Row row : sheet){
                rowNumber++;
                if(rowNumber == 1)
                    continue;
                try {
                    ParsedRow parsed = parseAndValidateRow(row, defaultHashPassword, dataFormatter);
                    batchBuffer.add(parsed.params());
                    newlyCreated.add(new NewEmployeeInfo(parsed.employeeId(), parsed.email(), parsed.username()));

                    if(batchBuffer.size() >= BATCH_SIZE){
                        self.executeTransactionalBatch(sql, batchBuffer);
                        batchBuffer.clear();
                        publishBatchEvents(newlyCreated, id);
                        newlyCreated = new ArrayList<>();
                    }
                } catch (BatchRowException e) {
                    e.setRowNumber(rowNumber);
                    errorList.add(e);
                }
            }

            String batchJobLog = "INSERT INTO batch_job_status (id, status, total_rows, created_by, created_at) " +
                    "VALUES(:id, :status, :totalRows, :createdBy, :createdAt)";

            MapSqlParameterSource batchJobRowParams = new MapSqlParameterSource();
            batchJobRowParams.addValue("id", id);
            batchJobRowParams.addValue("status", "COMPLETED");
            batchJobRowParams.addValue("totalRows", rowNumber - 1); // subtract header row
            batchJobRowParams.addValue("createdBy", "SYSTEM");
            batchJobRowParams.addValue("createdAt", LocalDateTime.now());

            self.executeTransactionalBatch(batchJobLog, Collections.singletonList(batchJobRowParams));

            if(!batchBuffer.isEmpty()){
                self.executeTransactionalBatch(sql, batchBuffer);
                batchBuffer.clear();
                publishBatchEvents(newlyCreated, id);
            }
            if(!errorList.isEmpty()){
                batchErrorLogger.handleErrorLogs(id, errorList);
                errorList.clear();
            }
        }
    }

    public ParsedRow parseAndValidateRow(Row row, String defaultPassword, DataFormatter dataFormatter) throws BatchRowException {
        MapSqlParameterSource p = new MapSqlParameterSource();

        Cell nameCell = row.getCell(0);
        String nameVal = (nameCell == null) ? "" : dataFormatter.formatCellValue(nameCell).trim();
        if(nameVal.isBlank()) {
            throw new BatchRowException("ERR_VAL_REQUIRED", "Name", "NULL", "Employee name column is missing");
        }
        p.addValue("name", nameVal);

        Cell emailCell = row.getCell(1);
        String emailVal = (emailCell == null) ? "" : dataFormatter.formatCellValue(emailCell).trim();
        if (emailVal.isEmpty()) {
            throw new BatchRowException("ERR_VAL_REQUIRED", "email", "NULL", "Email address is missing.");
        } else if (!emailVal.contains("@")) {
            throw new BatchRowException("ERR_VAL_FORMAT", "email", emailVal, "The email address string format is invalid.");
        }
        p.addValue("email", emailVal);

        UUID employeeId = UUID.randomUUID();
        p.addValue("id", employeeId);

        Cell roleCell = row.getCell(2);
        String roleVal = (roleCell == null) ? "ROLE_EMPLOYEE" : dataFormatter.formatCellValue(roleCell).toUpperCase().trim();
        if (roleVal.isBlank()) throw new BatchRowException("ERR_VAL_REQUIRED","Role","NULL","Role is missing");

        if (!roleVal.startsWith("ROLE_")) {
            roleVal = "ROLE_" + roleVal;
        }

        if (!roleVal.equals("ROLE_EMPLOYEE") && !roleVal.equals("ROLE_ADMIN")) {
            throw new BatchRowException("ERR_VAL_FORMAT","Role",roleVal,"The Role format is incorrect");
        }
        p.addValue("role", roleVal);

        p.addValue("password", defaultPassword);

        Cell addressCell = row.getCell(3);
        String addressVal = (addressCell == null) ? "NOT DEFINED" : dataFormatter.formatCellValue(addressCell).trim();
        if (addressVal.isBlank()) throw new BatchRowException("ERR_VAL_REQUIRED","Address","NULL","Address is missing");
        p.addValue("address", addressVal);

        Cell dobCell = row.getCell(4);
        String dobStr = (dobCell == null) ? "" : dataFormatter.formatCellValue(dobCell).trim();
        try {
            p.addValue("dob", LocalDate.parse(dobStr));
        } catch (Exception e) {
            throw new BatchRowException("ERR_TYPE_COERCION", "date_of_birth", dobStr.isEmpty() ? "NULL" : dobStr, "Unable to convert value to structural ISO date formatting (YYYY-MM-DD).");
        }

        Cell deptCell = row.getCell(5);
        String deptStr = (deptCell == null) ? "" : dataFormatter.formatCellValue(deptCell).trim();
        try {
            p.addValue("deptId", Integer.parseInt(deptStr));
            if(deptStr.isEmpty()) throw new BatchRowException("ERR_VAL_REQUIRED","Department","NULL",
                    "Department is missing");
            else if(!isInteger(deptStr)) throw new BatchRowException
                    ("ERR_VAL_FORMAT","Department",deptStr,"Department value must be Integer");
        } catch (Exception e) {
            throw new BatchRowException("ERR_TYPE_COERCION", "department_id", deptStr.isEmpty() ? "NULL" : deptStr, "Target numerical identifier mapping must be an integer string.");
        }

        p.addValue("enabled", false);

        return new ParsedRow(p, employeeId, emailVal, nameVal);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void executeTransactionalBatch(String sql, List<MapSqlParameterSource> parameters) {
        namedParameterJdbcTemplate.batchUpdate(sql, parameters.toArray(new MapSqlParameterSource[0]));
    }

    private void publishBatchEvents(List<NewEmployeeInfo> batch, UUID jobId) {
        if (batch.isEmpty()) return;
        try {
            eventPublisher.publishEvent(new ExcelImportEvent(this, batch, jobId));
            log.info("Published import event for {} employees", batch.size());
        } catch (Exception ex) {
            log.warn("Failed to publish batch import event: {}", ex.getMessage());
        }
    }

    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public record ParsedRow(MapSqlParameterSource params, UUID employeeId, String email, String username) {}
}