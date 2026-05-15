package com.gm.hrms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gm.hrms.enums.DayType;
import com.gm.hrms.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long personalId;

    @ManyToOne(fetch = FetchType.LAZY)
    private LeaveType leaveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private LeavePolicy leavePolicy;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private DayType startDayType;

    @Enumerated(EnumType.STRING)
    private DayType endDayType;

    private Double totalDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private Integer approvalLevel;

    private Long approvedBy;
    private LocalDateTime approvedAt;

    private String rejectionReason;

    private Boolean isCancelled;
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 300)
    private String cancelReason;

    @Column(name = "active_days")
    private Double activeDays;

}