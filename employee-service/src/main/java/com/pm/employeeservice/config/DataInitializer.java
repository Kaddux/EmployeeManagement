package com.pm.employeeservice.config;

import com.pm.employeeservice.model.Department;
import com.pm.employeeservice.model.Employee;
import com.pm.employeeservice.Enum.Role;
import com.pm.employeeservice.repository.DepartmentRepository;
import com.pm.employeeservice.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed IT department if not exists
        if (!departmentRepository.existsByDepartment_code("IT-01")) {
            Department dept = new Department();
            dept.setDepartment_name("Information Technology");
            dept.setDepartment_code("IT-01");
            departmentRepository.save(dept);
        }

        Department itDept = departmentRepository.findAll().stream()
                .filter(d -> "IT-01".equals(d.getDepartment_code()))
                .findFirst().orElse(null);

        // Seed test admin user if not exists
        if (!employeeRepository.existsByEmail("admin2@test.com")) {
            Employee admin = new Employee();
            admin.setName("Test Admin");
            admin.setEmail("admin2@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setAddress("Test Address");
            admin.setDate_of_birth(LocalDate.of(1990, 1, 1));
            admin.setDepartment(itDept);
            employeeRepository.save(admin);
            System.out.println("TEST ADMIN USER CREATED: admin2@test.com / admin123");
        }
    }
}
