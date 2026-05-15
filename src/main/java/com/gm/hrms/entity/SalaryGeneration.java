package com.gm.hrms.entity;

import com.gm.hrms.enums.SalaryGenerationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "salary_generations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_salary_gen_month_year",
                columnNames = {"month", "year"}
        )
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SalaryGeneration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer month;   // 1–12

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryGenerationStatus status;

    @Column(name = "total_employees")
    private Integer totalEmployees;

    @Column(name = "total_gross_payout")
    private Double totalGrossPayout;

    @Column(name = "total_net_payout")
    private Double totalNetPayout;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @OneToMany(
            mappedBy    = "salaryGeneration",
            cascade     = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalarySlip> salarySlips;
}