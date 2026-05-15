package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_eligibility_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveEligibilityRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  POLICY LINK (ONE RULE PER POLICY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    //  PROBATION
    @Column(nullable = false, name = "probation_period_in_months")
    private Integer probationPeriodInMonths;

    @Column(nullable = false, name = "allow_comp_off")
    private Boolean allowCompOff;

    //  SYSTEM CONTROL
    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(nullable = false, name = "is_system_defined")
    private Boolean isSystemDefined;
}