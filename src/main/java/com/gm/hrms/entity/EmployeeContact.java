package com.gm.hrms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_contacts")
public class EmployeeContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "office_email")
    private String officeEmail;

    @Column(name = "personal_phone")
    private String personalPhone;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}