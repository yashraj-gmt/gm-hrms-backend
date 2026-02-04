package com.gm.hrms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "employee_auth",
        indexes = {
                @Index(name = "idx_auth_username", columnList = "username"),
                @Index(name = "idx_auth_employee", columnList = "employee_id")
        }
)
public class EmployeeAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1–1 mapping with Employee
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    // Login identifier (office email)
    @Column(nullable = false, unique = true)
    private String username;

    // BCrypt / Argon2 hashed password
    @Column(nullable = false)
    private String passwordHash;

    // Account status
    @Column(nullable = false)
    private Boolean active = true;

    // Security controls
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false)
    private Boolean accountLocked = false;

    // Audit snapshot (not full logs)
    private LocalDateTime lastLoginAt;
}
