package com.gm.hrms.service.impl;

import com.gm.hrms.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:GM HRMS}")
    private String appName;

    // ════════════════════ SEND CREDENTIALS ═══════════════════════════════════

    @Async
    @Override
    public void sendCredentials(String to, String name, String password) {

        String subject = appName + " - Your Login Credentials";

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:32px;
                            border:1px solid #e5e7eb;border-radius:12px;background:#fff;">
                  <h2 style="color:#C35E33;margin-top:0;">Welcome to %s, %s!</h2>
                  <p style="color:#374151;">Your account has been created by HR.
                     Use the credentials below to log in.</p>
                  <div style="background:#FDF5F1;border-radius:8px;padding:20px;margin:20px 0;">
                    <p style="margin:4px 0;color:#374151;"><strong>Username / Email:</strong> %s</p>
                    <p style="margin:4px 0;color:#374151;"><strong>Temporary Password:</strong> %s</p>
                  </div>
                  <p style="color:#6b7280;font-size:13px;">
                    Please log in and change your password immediately.</p>
                  <p style="color:#6b7280;font-size:12px;margin-top:32px;">
                    — HR Team, %s</p>
                </div>
                """.formatted(appName, name, to, password, appName);

        sendHtml(to, subject, html);
    }

    // ════════════════════════ SEND OTP ═══════════════════════════════════════

    @Async
    @Override
    public void sendOtp(String to, String name, String otp) {

        String subject = appName + " - Password Reset OTP";

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:32px;
                            border:1px solid #e5e7eb;border-radius:12px;background:#fff;">
                  <h2 style="color:#C35E33;margin-top:0;">Password Reset Request</h2>
                  <p style="color:#374151;">Hi <strong>%s</strong>,</p>
                  <p style="color:#374151;">
                    We received a request to reset the password for your %s account.
                    Use the OTP below — it is valid for <strong>5 minutes</strong>.</p>
 
                  <div style="text-align:center;margin:32px 0;">
                    <div style="display:inline-block;background:#FDF5F1;border:2px dashed #C35E33;
                                border-radius:12px;padding:20px 40px;">
                      <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#C35E33;">
                        %s
                      </span>
                    </div>
                  </div>
 
                  <p style="color:#6b7280;font-size:13px;">
                    If you didn't request this, you can safely ignore this email.
                    Your password will not be changed.</p>
                  <p style="color:#6b7280;font-size:12px;margin-top:32px;">
                    — Security Team, %s</p>
                </div>
                """.formatted(name, appName, otp, appName);

        sendHtml(to, subject, html);
    }


    @Async
    @Override
    public void sendTransferOtp(String to, String name, String otp, String purpose) {

        String label   = purpose.replace("_", " ");
        String subject = appName + " - " + label + " OTP";

        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:32px;
                        border:1px solid #e5e7eb;border-radius:12px;background:#fff;">
              <h2 style="color:#C35E33;margin-top:0;">%s Authorization</h2>
              <p style="color:#374151;">Hi <strong>%s</strong>,</p>
              <p style="color:#374151;">
                A <strong>%s</strong> has been initiated on your account.
                Authorize it with the OTP below - valid for <strong>5 minutes</strong>.
              </p>
              <div style="text-align:center;margin:32px 0;">
                <div style="display:inline-block;background:#FDF5F1;border:2px dashed #C35E33;
                            border-radius:12px;padding:20px 40px;">
                  <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#C35E33;">
                    %s
                  </span>
                </div>
              </div>
              <p style="color:#ef4444;font-size:13px;">
                If you did NOT initiate this, contact your system administrator immediately.</p>
              <p style="color:#6b7280;font-size:12px;margin-top:32px;">
                — Security Team, %s</p>
            </div>
            """.formatted(label, name, label, otp, appName);

        sendHtml(to, subject, html);
    }

    @Async
    protected void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[EMAIL] Sent '{}' to {}", subject, to);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send to {}: {}", to, e.getMessage());
        }
    }

    // ── 1. Employee confirmation on apply ─────────────────────────────────────
    @Override
    public void sendLeaveAppliedEmployee(String to, String employeeName, String leaveType,
                                         String startDate, String endDate, double days) {
        String subject = appName + " - Leave Application Received";
        String body = buildEmailWrapper(employeeName,
                "Your leave application has been submitted successfully.",
                tableRow("Leave Type", leaveType) +
                        tableRow("From",       startDate) +
                        tableRow("To",         endDate) +
                        tableRow("Duration",   days + " day(s)") +
                        tableRow("Status",     "<span style='color:#854D0E;font-weight:600;'>Pending Approval</span>"),
                "You will receive a notification once your manager reviews the request."
        );
        send(to, subject, body);
    }

    // ── 2. Manager notification when employee applies ─────────────────────────
    @Override
    public void sendLeaveAppliedManager(String to, String employeeName, String leaveType,
                                        String startDate, String endDate, double days, String reason) {
        String subject = "Leave Request from " + employeeName + " - Action Required";
        String body = buildEmailWrapper("Manager",
                "<b>" + employeeName + "</b> has applied for leave. Please review and take action.",
                tableRow("Employee",   employeeName) +
                        tableRow("Leave Type", leaveType) +
                        tableRow("From",       startDate) +
                        tableRow("To",         endDate) +
                        tableRow("Duration",   days + " day(s)") +
                        tableRow("Reason",     reason),
                "Please log in to the HRMS portal to approve or reject this request."
        );
        send(to, subject, body);
    }

    // ── 3. Status update (approved / rejected) ────────────────────────────────
    @Override
    public void sendLeaveStatusUpdate(String to, String employeeName, String leaveType,
                                      String status, String startDate, String endDate, String remarks) {
        boolean approved = "APPROVED".equalsIgnoreCase(status);
        String color   = approved ? "#15803D" : "#DC2626";
        String subject = appName + " — Leave " + (approved ? "Approved" : "Rejected");
        String body = buildEmailWrapper(employeeName,
                "Your leave request has been <b style='color:" + color + ";'>" + status.toLowerCase() + "</b>.",
                tableRow("Leave Type", leaveType) +
                        tableRow("From",       startDate) +
                        tableRow("To",         endDate) +
                        tableRow("Status",     "<span style='color:" + color + ";font-weight:600;'>" + status + "</span>") +
                        (remarks != null && !remarks.isBlank() ? tableRow("Remarks", remarks) : ""),
                approved ? "Enjoy your leave!" : "For queries, please contact HR."
        );
        send(to, subject, body);
    }

    @Override
    public void sendLeaveCancelledManager(String to, String employeeName, String leaveType,
                                          String startDate, String endDate, String cancelReason) {
        String subject = "Leave Cancelled by " + employeeName;
        String body = buildEmailWrapper("Manager",
                "<b>" + employeeName + "</b> has cancelled their leave request.",
                tableRow("Leave Type",     leaveType) +
                        tableRow("From",           startDate) +
                        tableRow("To",             endDate) +
                        tableRow("Cancel Reason",  cancelReason),
                "No further action is required for this request."
        );
        send(to, subject, body);
    }

    // ── HTML helpers ──────────────────────────────────────────────────────────
    private String tableRow(String label, String value) {
        return "<tr>" +
                "<td style='padding:8px 12px;color:#6B7280;font-size:13px;width:130px;'>" + label + "</td>" +
                "<td style='padding:8px 12px;color:#111827;font-size:13px;font-weight:500;'>" + value + "</td>" +
                "</tr>";
    }

    private String buildEmailWrapper(String name, String headline, String tableRows, String footer) {
        return "<!DOCTYPE html><html><body style='margin:0;padding:0;background:#F9FAFB;font-family:Arial,sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:32px 16px;'>" +
                "<table width='560' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:12px;" +
                "border:1px solid #E5E7EB;overflow:hidden;'>" +
                // Header
                "<tr><td style='background:#C35E33;padding:24px 32px;'>" +
                "<p style='margin:0;color:#fff;font-size:20px;font-weight:700;'>Leave Management</p>" +
                "<p style='margin:4px 0 0;color:#FFD4BC;font-size:12px;'>HRMS Notification</p>" +
                "</td></tr>" +
                // Body
                "<tr><td style='padding:28px 32px;'>" +
                "<p style='margin:0 0 8px;font-size:15px;color:#374151;'>Hi <b>" + name + "</b>,</p>" +
                "<p style='margin:0 0 20px;font-size:14px;color:#6B7280;'>" + headline + "</p>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='background:#F9FAFB;" +
                "border:1px solid #E5E7EB;border-radius:8px;'>" + tableRows + "</table>" +
                "<p style='margin:20px 0 0;font-size:13px;color:#9CA3AF;'>" + footer + "</p>" +
                "</td></tr>" +
                // Footer
                "<tr><td style='background:#F9FAFB;padding:16px 32px;border-top:1px solid #E5E7EB;'>" +
                "<p style='margin:0;font-size:11px;color:#9CA3AF;'>This is an automated email. Please do not reply.</p>" +
                "</td></tr>" +
                "</table></td></tr></table></body></html>";
    }

    // ══════════════════════ INTERNAL HELPER ══════════════════════════════════

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message  = mailSender.createMimeMessage();
            MimeMessageHelper h  = new MimeMessageHelper(message, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(to);
            h.setSubject(subject);
            h.setText(html, true);
            mailSender.send(message);
            log.info("Email sent → {} [{}]", to, subject);
        } catch (MessagingException ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
            // Don't propagate — email failure should not break the API response
        }
    }
}