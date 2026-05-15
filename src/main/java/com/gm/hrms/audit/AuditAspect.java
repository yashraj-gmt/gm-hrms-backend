package com.gm.hrms.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogger auditLogger;

    @Around("@annotation(com.gm.hrms.audit.Auditable)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature sig      = (MethodSignature) pjp.getSignature();
        Auditable       auditable = sig.getMethod().getAnnotation(Auditable.class);

        String username  = resolveUsername();
        String ip        = resolveIp();
        long   startedAt = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();

            auditLogger.log(AuditLogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .action(auditable.action())
                    .resource(auditable.resource())
                    .username(username)
                    .ipAddress(ip)
                    .status(AuditLogEntry.AuditStatus.SUCCESS)
                    .durationMs(System.currentTimeMillis() - startedAt)
                    .description(auditable.description())
                    .build()
            );

            return result;

        } catch (Exception ex) {

            auditLogger.log(AuditLogEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .action(auditable.action())
                    .resource(auditable.resource())
                    .username(username)
                    .ipAddress(ip)
                    .status(AuditLogEntry.AuditStatus.FAILURE)
                    .durationMs(System.currentTimeMillis() - startedAt)
                    .description(auditable.description())
                    .errorMessage(ex.getMessage())
                    .build()
            );

            throw ex;   // never swallow — let GlobalExceptionHandler handle it
        }
    }

    // ──────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────

    private String resolveUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "anonymous";
    }

    private String resolveIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";

            HttpServletRequest request = attrs.getRequest();

            // Respect reverse-proxy headers (Railway, AWS ALB, Nginx…)
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();

        } catch (Exception ignored) {
            return "unknown";
        }
    }
}