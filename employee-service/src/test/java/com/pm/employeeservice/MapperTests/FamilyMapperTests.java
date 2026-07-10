package com.pm.employeeservice.MapperTests;

import com.pm.employeeservice.dto.FamilyRequestDTO;
import com.pm.employeeservice.dto.FamilyResponseDTO;
import com.pm.employeeservice.mapper.FamilyMapper;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FamilyMapperTests {

    @Test
    void toDTO_mapsAllFields() {
        Employee emp = new Employee();
        emp.setName("TEST EMPLOYEE");

        Family f = new Family();
        f.setFamily_id(UUID.randomUUID());
        f.setFather_name("TEST FATHER");
        f.setMother_name("TEST MOTHER");
        f.setEmployee(emp);

        FamilyResponseDTO dto = FamilyMapper.toDTO(f);
        assertEquals(f.getFamily_id(), dto.getFamilyId());
        assertEquals("TEST FATHER", dto.getFather_name());
        assertEquals("TEST MOTHER", dto.getMother_name());
        assertEquals("TEST EMPLOYEE", dto.getEmployee_name());
    }

    @Test
    void toDTO_whenEmployeeNull_doesNotThrow() {
        Family f = new Family();
        f.setFamily_id(UUID.randomUUID());
        f.setFather_name("TEST FATHER");
        f.setEmployee(null);

        assertDoesNotThrow(() -> FamilyMapper.toDTO(f));
        assertNull(FamilyMapper.toDTO(f).getEmployee_name());
    }

    @Test
    void toModel_mapsAllFields() {
        FamilyRequestDTO dto = new FamilyRequestDTO();
        dto.setFather_name("TEST FATHER");
        dto.setMother_name("TEST MOTHER");
        dto.setNumber_of_members(4);

        Family f = FamilyMapper.toModel(dto);
        assertEquals("TEST FATHER", f.getFather_name());
        assertEquals("TEST MOTHER", f.getMother_name());
        assertEquals("4", f.getNumber_of_members());
        assertNull(f.getEmployee());
        assertNull(f.getFamily_id());
    }
}
