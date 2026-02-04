package com.gm.hrms.entity;

import com.gm.hrms.enums.AuthEventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "auth_audit_logs",
        indexes = {
                @Index(name = "idx_audit_employee", columnList = "employee_id"),
                @Index(name = "idx_audit_event", columnList = "event_type"),
                @Index(name = "idx_audit_time", columnList = "event_time")
        }
)
public class AuthAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who performed the action
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // What happened
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AuthEventType eventType;
    /*
        LOGIN_SUCCESS
        LOGIN_FAILURE
        LOGOUT
        PASSWORD_CHANGED
        ACCOUNT_LOCKED
        ACCOUNT_UNLOCKED
    */

    // When it happened
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    // From where
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    // Extra info (optional)
    @Column(name = "remarks", length = 500)
    private String remarks;
}
