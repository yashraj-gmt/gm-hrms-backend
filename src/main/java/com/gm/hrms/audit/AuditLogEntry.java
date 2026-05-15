package com.gm.hrms.audit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogEntry {

    private final LocalDateTime timestamp;
    private final AuditAction   action;
    private final String        resource;
    private final String        username;
    private final String        ipAddress;
    private final AuditStatus   status;
    private final long          durationMs;
    private final String        description;
    private final String        errorMessage;   // null on success

    public enum AuditStatus {
        SUCCESS, FAILURE
    }
}