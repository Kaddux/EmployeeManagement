package com.pm.employeeservice.Excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
@RequiredArgsConstructor
public class EmployeeExcelExporter {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void streamExport(OutputStream outputStream) throws IOException{
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)){
            workbook.setCompressTempFiles(true);
            //SXSSF Workbook is an APACHE POI API to deal with large excel data
            //Implements Sliding Window
            //Keeps only a configurable number of rows in memory and flushes the rest to temporary files on disk.
            SXSSFSheet sheet = workbook.createSheet("ActiveEmployees");

            //Create Headers for Sheet
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID","Name","Email","Role","Address",
                    "DateOfBirth","DepartmentID","Enabled"};
            for(int i = 0; i < headers.length; i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            String sql = "SELECT id, name, email, role, address, date_of_birth, " +
                    "department_id, enabled FROM employee WHERE enabled = true";
            jdbcTemplate.getJdbcTemplate().execute((Connection con) ->{
                    try(PreparedStatement statement = con.prepareStatement(sql)){
                        statement.setFetchSize(1000);
                    try( ResultSet resultSet = statement.executeQuery()){
                        int rowIdx = 1;
                        while(resultSet.next()){
                            Row row = sheet.createRow(rowIdx++);
                            row.createCell(0).setCellValue(resultSet.getString("id"));
                            row.createCell(1).setCellValue(resultSet.getString("name"));
                            row.createCell(2).setCellValue(resultSet.getString("email"));
                            row.createCell(3).setCellValue(resultSet.getString("role"));
                            row.createCell(4).setCellValue(resultSet.getString("address"));
                            row.createCell(5).setCellValue(resultSet.getString("date_of_birth"));
                            row.createCell(6).setCellValue(resultSet.getString("department_id"));
                            row.createCell(7).setCellValue(resultSet.getString("enabled"));
                    }
                    }
                    }
                    return null;

        });
            workbook.write(outputStream);
        }finally {
            outputStream.flush();
        }
    }
}
