package com.gm.hrms.entity;

import com.gm.hrms.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "employee_code")
    private String employeeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role;

    // ================= MODULES =================

    // Employment Module
    @OneToOne(mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private EmployeeEmployment employment;


    // LINK TO ROOT IDENTITY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;
}