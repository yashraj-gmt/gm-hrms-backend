package com.gm.hrms.entity;

import com.gm.hrms.enums.PayrollComponentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_slip_components")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SalarySlipComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_slip_id", nullable = false)
    private SalarySlip salarySlip;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Column(name = "component_code")
    private String componentCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollComponentType type;   // EARNING | DEDUCTION

    @Column(nullable = false)
    private Double amount;

    @Column(name = "display_order")
    private Integer displayOrder;
}