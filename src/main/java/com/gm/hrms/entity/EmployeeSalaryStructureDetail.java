package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_salary_structure_details")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeSalaryStructureDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_structure_id", nullable = false)
    private EmployeeSalaryStructure salaryStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_component_id", nullable = false)
    private PayrollComponent payrollComponent;

    @Column(nullable = false)
    private Double amount;
}