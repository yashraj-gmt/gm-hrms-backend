package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "employee_salary_structures",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_salary_str_person_from",
                columnNames = {"personal_information_id", "effective_from"}
        )
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeSalaryStructure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(nullable = false, name = "monthly_ctc")
    private Double monthlyCTC;

    @Column(nullable = false, name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @OneToMany(
            mappedBy    = "salaryStructure",
            cascade     = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EmployeeSalaryStructureDetail> details;
}