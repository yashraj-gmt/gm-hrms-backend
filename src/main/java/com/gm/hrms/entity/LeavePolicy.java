package com.gm.hrms.entity;

import com.gm.hrms.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeavePolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC =================
    @Column(name = "policy_name")
    private String policyName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    // ================= RULES =================
    private Boolean requiresApproval;        // approval required or not

    private Boolean allowHalfDay;            // permission (with LeaveType check)

    private Boolean allowBackdatedLeave;

    private Boolean sandwichRuleEnabled;

    // ================= SYSTEM =================
    private Boolean isActive;

    private Boolean isSystemDefined;

    private LocalDate deletedAt;
}