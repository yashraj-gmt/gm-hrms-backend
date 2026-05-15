package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.enums.TimesheetStatus;
import com.gm.hrms.service.TimesheetReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/timesheets")
@RequiredArgsConstructor
public class TimesheetReportController {

    private final TimesheetReportService service;

    // ================= DATE RANGE =================
    @GetMapping("/date-range")
    @Auditable(
            action      = AuditAction.EXPORT_REPORT,
            resource    = "TimesheetReport",
            description = "Export timesheets by date range"
    )
    public ResponseEntity<?> dateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(service.byDateRange(startDate, endDate));
    }

    // ================= BY EMPLOYEE =================
    @GetMapping("/employee/{id}")
    public ResponseEntity<?> employee(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(service.byEmployee(id, startDate, endDate));
    }

    // ================= BY PROJECT =================
    @GetMapping("/project/{id}")
    public ResponseEntity<?> project(
            @PathVariable Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(service.byProject(id, startDate, endDate));
    }

    // ================= BY STATUS =================
    @GetMapping("/status/{status}")
    @Auditable(
            action      = AuditAction.EXPORT_REPORT,
            resource    = "TimesheetReport",
            description = "Export timesheets by status"
    )
    public ResponseEntity<?> status(
            @PathVariable TimesheetStatus status,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(service.byStatus(status, startDate, endDate));
    }

    // ================= TODAY ALL =================
    @GetMapping("/today")
    @Auditable(
            action      = AuditAction.EXPORT_REPORT,
            resource    = "TimesheetReport",
            description = "Export all timesheets for today"
    )
    public ResponseEntity<?> today() {
        return ResponseEntity.ok(service.todayAll());
    }

    // ================= TODAY BY EMPLOYEE =================
    @GetMapping("/today/employee/{id}")
    public ResponseEntity<?> todayEmp(@PathVariable Long id) {
        return ResponseEntity.ok(service.todayByEmployee(id));
    }

    // ================= TODAY BY PROJECT =================
    @GetMapping("/today/project/{id}")
    public ResponseEntity<?> todayProj(@PathVariable Long id) {
        return ResponseEntity.ok(service.todayByProject(id));
    }

    // ================= MONTHLY =================
    @GetMapping("/monthly")
    @Auditable(
            action      = AuditAction.EXPORT_REPORT,
            resource    = "TimesheetReport",
            description = "Export monthly timesheet report (all employees)"
    )
    public ResponseEntity<?> monthly(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(service.monthly(month, year));
    }

    // ================= MONTHLY BY EMPLOYEE =================
    @GetMapping("/monthly/employee/{id}")
    public ResponseEntity<?> monthlyEmp(
            @PathVariable Long id,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(service.monthlyEmployee(id, month, year));
    }
}