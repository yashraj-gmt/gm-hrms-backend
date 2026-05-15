package com.gm.hrms.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class AuditLogger {

    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");

    private static final String FMT =
            "ACTION={} | RESOURCE={} | USER={} | IP={} | STATUS={} | DURATION={}ms | DESC={} | ERROR={}";

    public void log(AuditLogEntry entry) {
        if (entry.getStatus() == AuditLogEntry.AuditStatus.SUCCESS) {
            AUDIT.info(FMT,
                    entry.getAction(),
                    entry.getResource(),
                    entry.getUsername(),
                    entry.getIpAddress(),
                    entry.getStatus(),
                    entry.getDurationMs(),
                    entry.getDescription(),
                    "-"
            );
        } else {
            AUDIT.warn(FMT,
                    entry.getAction(),
                    entry.getResource(),
                    entry.getUsername(),
                    entry.getIpAddress(),
                    entry.getStatus(),
                    entry.getDurationMs(),
                    entry.getDescription(),
                    entry.getErrorMessage()
            );
        }
    }
}