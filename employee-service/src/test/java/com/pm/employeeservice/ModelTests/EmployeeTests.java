package com.pm.employeeservice.ModelTests;

import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTests {

    private Employee createEmployee(Role role, boolean enabled) {
        Employee employee = new Employee();
        employee.setRole(role);
        employee.setEnabled(enabled);

        return employee;
    }

    @Test
    void whenEnabledFalse_returnsFalse(){
        Employee employee = createEmployee(Role.ROLE_ADMIN,false);
        employee.activate();
        assertTrue(employee.isEnabled());
    }
    @Test
    void whenEnabledTrue_throwsException(){
        Employee employee = createEmployee(Role.ROLE_ADMIN,true);
        employee.setEnabled(true);

        assertThrows(IllegalStateException.class, employee::activate);

    }
    @Test
    void changeRole_toDifferentRole_succeeds(){
        Employee employee = createEmployee(Role.ROLE_EMPLOYEE,true);
        employee.setRole(Role.ROLE_ADMIN);

        assertEquals(Role.ROLE_ADMIN, employee.getRole());
    }
    @Test
    void changeRole_toSameRole_throwsException(){
        Employee employee = createEmployee(Role.ROLE_ADMIN,true);

        assertThrows(IllegalStateException.class,() -> employee.changeRole(Role.ROLE_ADMIN));
    }


    @Test
    void assignDepartment_toDifferentDepartment_succeeds() {
        Employee e = createEmployee(Role.ROLE_EMPLOYEE, true);
        Department d1 = new Department(); d1.setDepartment_id(1);
        Department d2 = new Department(); d2.setDepartment_id(2);
        e.assignDepartment(d1);
        e.assignDepartment(d2);
        assertEquals(d2, e.getDepartment());
    }

    @Test
    void assignDepartment_toSameDepartment_throws() {
        Employee e = createEmployee(Role.ROLE_EMPLOYEE, true);
        Department d = new Department(); d.setDepartment_id(1);
        e.assignDepartment(d);
        assertThrows(IllegalStateException.class, () -> e.assignDepartment(d));
    }

    @Test
    void changePassword_updatesPassword() {
        Employee e = createEmployee(Role.ROLE_EMPLOYEE, true);
        e.changePassword("encoded-new-password");
        assertEquals("encoded-new-password", e.getPassword());
    }

    @Test
    void updateProfile_updatesAllAllowedFields() {
        Employee e = createEmployee(Role.ROLE_EMPLOYEE, true);
        Department d = new Department(); d.setDepartment_id(3);
        e.updateProfile("New Name", "New Address", LocalDate.of(1990, 1, 1), "new@email.com", d);
        assertEquals("New Name", e.getName());
        assertEquals("New Address", e.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), e.getDate_of_birth());
        assertEquals("new@email.com", e.getEmail());
        assertEquals(d, e.getDepartment());
    }
}

