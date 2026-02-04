package com.gm.hrms.entity;

import com.gm.hrms.enums.AttendanceAuditEventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance_audit_logs",
        indexes = {
                @Index(name = "idx_att_audit_employee", columnList = "employee_id"),
                @Index(name = "idx_att_audit_attendance", columnList = "attendance_id"),
                @Index(name = "idx_att_audit_time", columnList = "event_time"),
                @Index(name = "idx_att_audit_type", columnList = "event_type")
        }
)
public class AttendanceAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee whose attendance is affected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Attendance record reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    // Action type
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AttendanceAuditEventType eventType;

    // When action happened
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    // Who performed the action (Admin / HR / Self)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private Employee performedBy;

    // Optional details
    @Column(length = 500)
    private String remarks;
}
