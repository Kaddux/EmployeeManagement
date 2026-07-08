package com.pm.employeeservice.Exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BatchErrorLogger {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleErrorLogs(UUID jobId, List<BatchRowException> errors){
        String sql = "INSERT INTO batch_row_error(job_id, row_number, error_code, column_name, rejected_value, error_message) " +
                "VALUES(:jobId, :rowNumber, :errorCode, :columnName, :rejectedValue, :errorMessage)";
        List<MapSqlParameterSource> batch = new ArrayList<>(errors.size());

        for(BatchRowException error : errors){
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("jobId",jobId);
            params.addValue("rowNumber",error.getRowNumber());
            params.addValue("errorCode",error.getErrorCode());
            params.addValue("columnName",error.getColumnName());
            params.addValue("rejectedValue",error.getRejectedValue());
            params.addValue("errorMessage",error.getMessage());
            batch.add(params);
        }
        jdbcTemplate.batchUpdate(sql,batch.toArray(new MapSqlParameterSource[0]));
    }
}
