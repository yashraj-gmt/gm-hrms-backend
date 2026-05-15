package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carry_forward_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarryForwardRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // POLICY LINK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULE =================

    @Column(nullable = false, name = "is_enabled")
    private Boolean isEnabled;

    @Column(nullable = false, name = "max_carry_forward")
    private Integer maxCarryForward;   // e.g. max 5 leaves

    @Column(name = "expiry_days")
    private Integer expiryDays;        // optional expiry

    // ================= SYSTEM =================

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(nullable = false, name = "is_system_defined")
    private Boolean isSystemDefined;
}