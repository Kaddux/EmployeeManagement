package com.pm.employeeservice.Excel;

import com.github.pjfanning.xlsx.StreamingReader;
import com.pm.employeeservice.Exceptions.BatchErrorLogger;
import com.pm.employeeservice.Exceptions.BatchRowException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.xmlbeans.impl.tool.XMLBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeeExcelImporter {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final int BATCH_SIZE = 200;
    private final XMLBean.ErrorLogger errorLogger;
    private final PasswordEncoder passwordEncoder;
    private final BatchErrorLogger batchErrorLogger;

    public void streamImport(InputStream inputStream, UUID id) throws IOException {
        String sql ="INSERT INTO employee (id, name, email, role, password, address, date_of_birth, department_id, enabled) " +
                "VALUES (:id, :name, :email, :role, :password, :address, :dob, :deptId, :enabled)";
        List<MapSqlParameterSource> batchBuffer = new ArrayList<>(BATCH_SIZE);
        List<BatchRowException> errorList = new ArrayList<>();
        String defaultHashPassword = passwordEncoder.encode("TemporaryDefault");
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
                    MapSqlParameterSource rowParams = parseAndValidateRow(row,defaultHashPassword);
                    batchBuffer.add(rowParams);
                    if(batchBuffer.size() >= BATCH_SIZE){
                        executeTransactionalBatch(sql,batchBuffer);
                    }
                } catch (BatchRowException e) {
                    e.setRowNumber(rowNumber);
                    errorList.add(e);
                }
                if(batchBuffer.isEmpty())
                    executeTransactionalBatch(sql,batchBuffer);
                if(!errorList.isEmpty())
                    batchErrorLogger.handleErrorLogs(id,errorList);
            }
        }
    }
    public MapSqlParameterSource parseAndValidateRow(Row row, String defaultPassword) throws BatchRowException {
        MapSqlParameterSource p = new MapSqlParameterSource();

        Cell nameCell = row.getCell(0);
        if(nameCell == null || nameCell.getStringCellValue().isBlank()) {
            throw new BatchRowException("ERR_VAL_REQUIRED", "Name", "NULL", "Employee name column is missing");
        }
        Cell emailCell = row.getCell(1);
        String emailVal = (emailCell == null) ? "" : emailCell.getStringCellValue().trim();
        if (emailVal.isEmpty()) {
            throw new BatchRowException("ERR_VAL_REQUIRED", "email", "NULL", "Email address is missing.");
        } else if (!emailVal.contains("@")) {
            throw new BatchRowException("ERR_VAL_FORMAT", "email", emailVal, "The email address string format is invalid.");
        }
        p.addValue("id", UUID.randomUUID());
        p.addValue("role", row.getCell(2) != null ? row.getCell(2).getStringCellValue().toUpperCase().trim() : "EMPLOYEE");
        p.addValue("password", defaultPassword);
        p.addValue("address", row.getCell(3) != null ? row.getCell(3).getStringCellValue().trim() : "NOT DEFINED");

        Cell dobCell = row.getCell(4);
        try {
            p.addValue("dob", LocalDate.parse(dobCell.getStringCellValue().trim()));
        } catch (Exception e) {
            throw new BatchRowException("ERR_TYPE_COERCION", "date_of_birth", dobCell == null ? "NULL" : dobCell.getStringCellValue(), "Unable to convert value to structural ISO date formatting (YYYY-MM-DD).");
        }

        Cell deptCell = row.getCell(5);
        try {
            p.addValue("deptId", Integer.parseInt(deptCell.getStringCellValue().trim()));
        } catch (Exception e) {
            throw new BatchRowException("ERR_TYPE_COERCION", "department_id", deptCell == null ? "NULL" : deptCell.getStringCellValue(), "Target numerical identifier mapping must be an integer string.");
        }

        p.addValue("enabled", true);
        return p;

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void executeTransactionalBatch(String sql, List<MapSqlParameterSource> parameters) {
       namedParameterJdbcTemplate.batchUpdate(sql, parameters.toArray(new MapSqlParameterSource[0]));
    }
    }
