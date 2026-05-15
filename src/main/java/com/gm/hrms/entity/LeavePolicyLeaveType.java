package com.gm.hrms.entity;

import com.gm.hrms.enums.AccrualType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_policy_leave_types",
        uniqueConstraints = @UniqueConstraint(columnNames = {"policy_id", "leave_type_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeavePolicyLeaveType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  POLICY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    //  LEAVE TYPE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    //  CORE FIELDS
    private Integer totalLeaves;

    @Enumerated(EnumType.STRING)
    private AccrualType accrualType;   // MONTHLY / YEARLY

    private Integer accrualValue;

    // SYSTEM
    private Boolean isActive;
}