package com.pm.employeeservice.MapperTests;

import com.pm.employeeservice.dto.DepartmentRequestDTO;
import com.pm.employeeservice.dto.DepartmentResponseDTO;
import com.pm.employeeservice.mapper.DepartmentMapper;
import com.pm.employeeservice.model.Department;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DepartmentMapperTests {
    @Test
    void toDTO_mapsAllFields() {
        Department d = new Department();
        d.setDepartment_id(1);
        d.setDepartment_name("HR");
        d.setDepartment_code("HR001");

        DepartmentResponseDTO dto = DepartmentMapper.toDTO(d);
        assertEquals(1, dto.getDepartment_id());
        assertEquals("HR", dto.getDepartment_name());
        assertEquals("HR001", dto.getDepartment_code());
    }

    @Test
    void toModel_mapsAllFields() {
        DepartmentRequestDTO dto = new DepartmentRequestDTO();
        dto.setDepartment_name("Finance");
        dto.setDepartment_code("FIN002");

        Department d = DepartmentMapper.toModel(dto);
        assertEquals("Finance", d.getDepartment_name());
        assertEquals("FIN002", d.getDepartment_code());
        assertNull(d.getDepartment_id());
    }
}
