package com.gm.hrms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "leave_application_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = "policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 POLICY LINK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private LeavePolicy leavePolicy;

    // ================= RULES =================

    @NotNull
    @Column(name = "allow_half_day")
    private Boolean allowHalfDay;

    @NotNull
    @DecimalMin(value = "0.5", message = "Minimum leave duration must be at least 0.5")
    @DecimalMax(value = "365", message = "Invalid leave duration")
    @Column(name = "min_leave_duration")
    private Double minLeaveDuration;

    @NotNull
    @Min(value = 1, message = "Max consecutive days must be at least 1")
    @Max(value = 365, message = "Max consecutive days too large")
    @Column(name = "max_consecutive_days")
    private Integer maxConsecutiveDays;

    @NotNull
    @Min(value = 0, message = "Apply before days cannot be negative")
    @Max(value = 365, message = "Apply before days too large")
    @Column(name = "apply_before_days")
    private Integer applyBeforeDays;

    @NotNull
    @Column(name = "allow_back_dated_leave")
    private Boolean allowBackdatedLeave;

    // ================= SANDWICH =================

    @NotNull
    @Column(name = "sandwich_rule_enabled")
    private Boolean sandwichRuleEnabled;

    @Column(name = "include_holidays")
    private Boolean includeHolidays;

    @Column(name = "include_weekends")
    private Boolean includeWeekends;

    // ================= CONTROL =================

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    @NotNull
    @Column(name = "is_system_defined")
    private Boolean isSystemDefined;
}