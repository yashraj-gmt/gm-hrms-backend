package com.gm.hrms.entity;

import com.gm.hrms.enums.LeaveAuditEventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "leave_audit_logs",
        indexes = {
                @Index(name = "idx_leave_audit_employee", columnList = "employee_id"),
                @Index(name = "idx_leave_audit_leave", columnList = "leave_application_id"),
                @Index(name = "idx_leave_audit_time", columnList = "event_time"),
                @Index(name = "idx_leave_audit_event", columnList = "event_type")
        }
)
public class LeaveAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee whose leave is affected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Leave application reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_application_id", nullable = false)
    private LeaveApplication leaveApplication;

    // What happened
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private LeaveAuditEventType eventType;

    // When it happened
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    // Who performed the action (Employee / HR / Admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private Employee performedBy;

    // Optional note
    @Column(length = 500)
    private String remarks;
}
