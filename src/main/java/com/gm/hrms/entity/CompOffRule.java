package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comp_off_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompOffRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // POLICY LINK (1 policy = 1 rule)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULE CONFIG =================

    @Column(nullable = false, name = "is_enabled")
    private Boolean isEnabled;

    @Column(nullable = false, name = "approval_required")
    private Boolean approvalRequired;

    @Column(nullable = false, name = "max_per_month")
    private Integer maxPerMonth;   // e.g. 2 comp off/month

    @Column(nullable = false, name = "expiry_days")
    private Integer expiryDays;    // e.g. 30 days validity

    // ================= SYSTEM =================

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(nullable = false, name = "is_system_defined")
    private Boolean isSystemDefined;
}