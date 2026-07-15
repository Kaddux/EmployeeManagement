package com.pm.employeeservice.ModelTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.model.Family;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FamilyTests {
    private Family createFamily(Role role, boolean enabled){
        UUID id = UUID.randomUUID();
        Employee employee = new Employee();
        employee.setRole(role);
        employee.setEnabled(true);
        Family family = new Family();
        family.setEmployee(employee);
        family.setFamily_id(id);
        family.setNumber_of_members("4");
        family.setFather_name("TEST FATHER");
        family.setMother_name("TEST MOTHER");

        return family;
    }
    @Test
    void familyWithoutEmployee_returnsException(){
        Family newfamily = createFamily(Role.ROLE_ADMIN,true);
        newfamily.setEmployee(null);

        assertThrows(Exception.class, (Executable) newfamily.getEmployee());
    }

    @Test
    void familyWithEmployee_returnsOK_when(){
        Family newfamily = createFamily(Role.ROLE_ADMIN,true);
        assertEquals("TEST FATHER", newfamily.getFather_name());
        assertEquals("TEST MOTHER", newfamily.getMother_name());
        assertTrue(newfamily.getEmployee().isEnabled());
    }

}
