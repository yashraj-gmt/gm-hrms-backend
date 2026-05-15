package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.SalaryGenerationRequestDTO;
import com.gm.hrms.dto.response.SalaryGenerationResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.SalarySlipMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.SalaryGenerationService;
import com.gm.hrms.util.NumberToWordsConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SalaryGenerationServiceImpl implements SalaryGenerationService {

    private final SalaryGenerationRepository      generationRepo;
    private final SalarySlipRepository            slipRepo;
    private final EmployeeSalaryStructureRepository structureRepo;
    private final AttendanceRepository            attendanceRepo;
    private final LeaveRequestRepository          leaveRequestRepo;
    private final HolidayRepository               holidayRepo;
    private final EmployeeRepository              employeeRepo;
    private final BankLegalDetailsRepository      bankRepo;
    private final NumberToWordsConverter          converter;

    // ──────────────────────────────────────────────────────────────────────
    // GENERATE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    public SalaryGenerationResponseDTO generate(SalaryGenerationRequestDTO dto) {

        if (generationRepo.existsByMonthAndYear(dto.getMonth(), dto.getYear())) {
            throw new DuplicateResourceException(
                    "Salary already generated for " + dto.getMonth() + "/" + dto.getYear()
                            + ". Delete it first or finalize it."
            );
        }

        LocalDate firstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        LocalDate lastDay  = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        // Working days in the month (Mon–Sat, excluding public holidays)
        int totalWorkingDays = calculateWorkingDays(firstDay, lastDay);

        // All employees who have an active salary structure this month
        List<EmployeeSalaryStructure> structures =
                structureRepo.findAllActiveStructures(firstDay);

        SalaryGeneration generation = SalaryGeneration.builder()
                .month(dto.getMonth())
                .year(dto.getYear())
                .status(SalaryGenerationStatus.DRAFT)
                .generatedAt(LocalDateTime.now())
                .build();

        List<SalarySlip> slips = new ArrayList<>();
        double totalGross = 0, totalNet = 0;

        for (EmployeeSalaryStructure structure : structures) {

            PersonalInformation personal = structure.getPersonalInformation();
            Long personalId = personal.getId();

            // Skip if salary slip already exists (re-generate guard)
            if (slipRepo.existsByPersonalInformationIdAndMonthAndYear(
                    personalId, dto.getMonth(), dto.getYear())) continue;

            // ─── Attendance summary ───
            AttendanceSummary summary = buildAttendanceSummary(
                    personalId, dto.getMonth(), dto.getYear(), firstDay, lastDay);

            double lopDays  = summary.lopDays();
            double paidDays = Math.max(0, totalWorkingDays - lopDays);

            // ─── Component-level calculation ───
            List<SalarySlipComponent> components = new ArrayList<>();
            double grossEarnings   = 0;
            double totalDeductions = 0;

            for (EmployeeSalaryStructureDetail detail : structure.getDetails()) {
                PayrollComponent comp = detail.getPayrollComponent();
                double baseAmount    = detail.getAmount();

                // Apply LOP proportionally to EARNINGS
                double finalAmount = baseAmount;
                if (comp.getType() == PayrollComponentType.EARNING && lopDays > 0) {
                    finalAmount = baseAmount * paidDays / totalWorkingDays;
                }
                finalAmount = round(finalAmount);

                components.add(SalarySlipComponent.builder()
                        .componentName(comp.getName())
                        .componentCode(comp.getCode())
                        .type(comp.getType())
                        .amount(finalAmount)
                        .displayOrder(comp.getDisplayOrder())
                        .build());

                if (comp.getType() == PayrollComponentType.EARNING)
                    grossEarnings += finalAmount;
                else
                    totalDeductions += finalAmount;
            }

            double netPayable = round(grossEarnings - totalDeductions);

            // ─── Snapshots ───
            Employee employee = employeeRepo
                    .findByPersonalInformationId(personalId).orElse(null);

            BankLegalDetails bank = bankRepo
                    .findByPersonalInformationId(personalId).orElse(null);

            WorkProfile wp = personal.getWorkProfile();

            String employeeName = personal.getFirstName() + " " + personal.getLastName();

            SalarySlip slip = SalarySlip.builder()
                    .salaryGeneration(generation)
                    .personalInformation(personal)
                    .employeeCode(employee != null ? employee.getEmployeeCode() : "")
                    .employeeName(employeeName)
                    .department(wp != null && wp.getDepartment() != null
                            ? wp.getDepartment().getName() : "")
                    .designation(wp != null && wp.getDesignation() != null
                            ? wp.getDesignation().getName() : "")
                    .gender(personal.getGender() != null ? personal.getGender().name() : "")
                    .dateOfJoining(wp != null ? wp.getDateOfJoining() : null)
                    .payDate(lastDay)
                    .bankName(bank != null ? bank.getBankName() : "")
                    .accountNumber(bank != null ? bank.getAccountNumber() : "")
                    .ifscCode(bank != null ? bank.getIfscCode() : "")
                    .panNumber(bank != null ? bank.getPanNumber() : "")
                    .pfNumber(bank != null ? bank.getPfNumber() : "")
                    .month(dto.getMonth())
                    .year(dto.getYear())
                    .totalWorkingDays(totalWorkingDays)
                    .paidDays(paidDays)
                    .lopDays(lopDays)
                    .grossEarnings(grossEarnings)
                    .totalDeductions(totalDeductions)
                    .netPayable(netPayable)
                    .netPayableWords(converter.convert(netPayable))
                    .status(SalarySlipStatus.DRAFT)
                    .components(new ArrayList<>())
                    .build();

            // Link components back to slip
            components.forEach(c -> c.setSalarySlip(slip));
            slip.getComponents().addAll(components);

            slips.add(slip);
            totalGross += grossEarnings;
            totalNet   += netPayable;
        }

        generation.setSalarySlips(slips);
        generation.setTotalEmployees(slips.size());
        generation.setTotalGrossPayout(round(totalGross));
        generation.setTotalNetPayout(round(totalNet));

        generationRepo.save(generation);

        return toResponse(generation);
    }

    // ──────────────────────────────────────────────────────────────────────
    // FINALIZE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    public SalaryGenerationResponseDTO finalizeSalary(Long generationId) {
        SalaryGeneration generation = generationRepo.findById(generationId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary generation not found"));

        if (generation.getStatus() == SalaryGenerationStatus.FINALIZED) {
            throw new InvalidRequestException("Salary already finalized");
        }

        generation.setStatus(SalaryGenerationStatus.FINALIZED);
        generation.setFinalizedAt(LocalDateTime.now());

        generation.getSalarySlips().forEach(slip ->
                slip.setStatus(SalarySlipStatus.GENERATED));

        generationRepo.save(generation);
        return toResponse(generation);
    }

    // ──────────────────────────────────────────────────────────────────────
    // QUERIES
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public SalaryGenerationResponseDTO getById(Long id) {
        return toResponse(generationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary generation not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryGenerationResponseDTO> getAll() {
        return generationRepo.findAllByOrderByYearDescMonthDesc()
                .stream().map(this::toResponse).toList();
    }

    // ──────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Counts Mon–Sat working days, minus public/national holidays.
     * Adjust if your shifts define custom week-offs.
     */
    private int calculateWorkingDays(LocalDate from, LocalDate to) {
        Set<LocalDate> holidays = new HashSet<>(
                holidayRepo.findByHolidayDateBetweenAndIsActiveTrueAndIsOptionalFalse(from, to)
                        .stream().map(Holiday::getHolidayDate).toList()
        );

        int count = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SUNDAY && !holidays.contains(d)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Attendance summary:
     *  - PRESENT attendance = 1 day
     *  - HALF_DAY attendance = 0.5 day
     *  - ABSENT / no record  = LOP
     *  - Approved paid leaves = not LOP
     *  - Approved unpaid leaves = LOP
     */
    private AttendanceSummary buildAttendanceSummary(
            Long personalId, int month, int year,
            LocalDate from, LocalDate to) {

        List<Attendance> attendances = attendanceRepo
                .findByPersonalInformationIdAndAttendanceDateBetween(personalId, from, to);

        double presentDays = 0;
        for (Attendance a : attendances) {
            if (a.getStatus() == AttendanceStatus.PRESENT)      presentDays += 1;
            else if (a.getStatus() == AttendanceStatus.HALF_DAY) presentDays += 0.5;
        }

        // Approved paid leaves do NOT reduce pay
        long approvedPaidLeaves = leaveRequestRepo
                .countApprovedPaidLeaves(personalId, from, to);

        // Approved unpaid leaves reduce pay (LOP)
        double unpaidLopLeaves = leaveRequestRepo
                .sumUnpaidApprovedLeaveDays(personalId, from, to);

        // Absent days (no attendance, no approved leave)
        double totalExpectedDays = calculateWorkingDays(from, to);
        double accountedDays     = presentDays + approvedPaidLeaves + unpaidLopLeaves;
        double absentDays        = Math.max(0, totalExpectedDays - accountedDays);

        double lopDays = unpaidLopLeaves + absentDays;

        return new AttendanceSummary(presentDays, lopDays);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private SalaryGenerationResponseDTO toResponse(SalaryGeneration g) {
        String monthYear = Month.of(g.getMonth())
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + g.getYear();
        return SalaryGenerationResponseDTO.builder()
                .id(g.getId())
                .month(g.getMonth())
                .year(g.getYear())
                .monthYear(monthYear)
                .status(g.getStatus())
                .totalEmployees(g.getTotalEmployees())
                .totalGrossPayout(g.getTotalGrossPayout())
                .totalNetPayout(g.getTotalNetPayout())
                .generatedAt(g.getGeneratedAt())
                .finalizedAt(g.getFinalizedAt())
                .salarySlips(g.getSalarySlips() == null ? List.of() :
                        g.getSalarySlips().stream().map(SalarySlipMapper::toResponse).toList())
                .build();
    }

    /** Internal DTO — not exposed via API */
    private record AttendanceSummary(double presentDays, double lopDays) {}
}