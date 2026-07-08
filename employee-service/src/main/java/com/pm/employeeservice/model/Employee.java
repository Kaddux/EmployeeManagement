package com.pm.employeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.pm.employeeservice.Enum.Role;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false,name = "email")
    @Email
    @NotNull
    private String email;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "address")
    private String address;

    @Column(name = "enabled")
    private boolean enabled;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Family> familyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<verificationTokens> tokens = new ArrayList<>();

    // Business logic: change role with validation
    public void changeRole(Role newRole) {
        if (this.role == newRole) {
            throw new IllegalStateException("Employee already has role: " + newRole);
        }
        this.role = newRole;
    }

    // Business logic: activate account
    public void activate() {
        if (this.enabled) {
            throw new IllegalStateException("Account already active");
        }
        this.enabled = true;
    }

    // Business logic: update profile (only allowed fields)
    public void updateProfile(String name, String address, LocalDate dateOfBirth, String email, Department department) {
        this.name = name;
        this.address = address;
        this.date_of_birth = dateOfBirth;
        this.email = email;
        this.department = department;
    }

    // Business logic: assign department
    public void assignDepartment(Department department) {
        if (this.department != null && this.department.equals(department)) {
            throw new IllegalStateException("Already assigned to this department");
        }
        this.department = department;
    }

    // Business logic: change password (called after verification)
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
