package com.gm.hrms.entity;

import com.gm.hrms.enums.PayrollComponentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_components")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PayrollComponent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollComponentType type;   // EARNING | DEDUCTION

    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(nullable = false, name = "is_system_defined")
    private Boolean isSystemDefined;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;
}