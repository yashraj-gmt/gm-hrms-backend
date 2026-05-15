package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveRequestMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final WorkProfileRepository workProfileRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTransactionService      transactionService;
    private final PersonalInformationRepository personalRepository;
    private final LeaveApplicationRuleRepository ruleRepository;
    private final LeavePolicyRepository policyRepository;
    private final LeavePolicyLeaveTypeRepository mappingRepository;
    private final LeaveBalanceService balanceService;
    LeaveBalanceRepository leaveBalanceRepository;
    private final EmailService emailService;
    private final LeaveAttendanceService leaveAttendanceService;
    private final LeaveValidationEngine validationEngine;
    private final LeaveRequestMapper mapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");


    // ================= APPLY =================
    @Override
    @Transactional
    public LeaveRequestResponseDTO apply(LeaveRequestDTO dto) {

        PersonalInformation personal = personalRepository.findById(dto.getPersonalId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findByIdAndIsActiveTrue(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        validateDates(dto.getStartDate(), dto.getEndDate());

        // 4. Calculate working days
        double totalDays = calculateWorkingDays(
                dto.getStartDate(), dto.getEndDate(),
                dto.getStartDayType(), dto.getEndDayType()
        );

        if (totalDays <= 0) {
            throw new InvalidRequestException("No working days in the selected range");
        }

        LeavePolicy policy = policyRepository
                .findByEmploymentTypeAndIsActiveTrue(personal.getEmploymentType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active leave policy for employment type: " + personal.getEmploymentType()));

        if (!Boolean.TRUE.equals(leaveType.getIsCompOff()) && leaveType.getIsPaid() != null) {
            checkAndDeductBalance(personal, leaveType, policy, totalDays);
        }

        int currentYear = LocalDate.now().getYear();
        LeaveRequest request = LeaveRequest.builder()
                .personalId(personal.getId())
                .leaveType(leaveType)
                .leavePolicy(policy)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startDayType(dto.getStartDayType())
                .endDayType(dto.getEndDayType())
                .totalDays(totalDays)
                .activeDays(totalDays)
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .approvalLevel(1)
                .isCancelled(false)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(request);

        String empEmail   = resolveEmail(personal);
        String empName    = fullName(personal);
        String startStr   = dto.getStartDate().format(FMT);
        String endStr     = dto.getEndDate().format(FMT);

        // Email to employee
        emailService.sendLeaveAppliedEmployee(
                empEmail, empName, leaveType.getName(), startStr, endStr, totalDays);

        // Email to manager / HR (fetch from work profile if available)
        resolveManagerEmail(personal).forEach(mgEmail ->
                emailService.sendLeaveAppliedManager(
                        mgEmail, empName, leaveType.getName(),
                        startStr, endStr, totalDays, dto.getReason()));

        return toResponse(saved, personal);
    }

    // ================= APPROVE =================
/*
    @Override
    public void approve(Long id, Long approverId) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.REJECTED || req.getStatus() == LeaveStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot approve this request");
        }

        req.setApprovalLevel(req.getApprovalLevel() + 1);

        if (isFinalApproval(req)) {

            req.setStatus(LeaveStatus.APPROVED);
            req.setApprovedBy(approverId);
            req.setApprovedAt(LocalDateTime.now());

            //  Attendance sync
            leaveAttendanceService.markLeaveAttendance(req);
        }

        leaveRequestRepository.save(req);
    }

*/

    @Override
    @Transactional
    public void approve(Long leaveRequestId, Long approverId) {

        LeaveRequest request = getActiveRequest(leaveRequestId);

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException(
                    "Only PENDING requests can be approved. Current: " + request.getStatus());
        }

        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovedBy(approverId);
        request.setApprovedAt(LocalDateTime.now());
        leaveRequestRepository.save(request);

        // Notify employee
        personalRepository.findById(request.getPersonalId()).ifPresent(emp -> {
            emailService.sendLeaveStatusUpdate(
                    resolveEmail(emp), fullName(emp),
                    request.getLeaveType().getName(),
                    "APPROVED",
                    request.getStartDate().format(FMT),
                    request.getEndDate().format(FMT),
                    "Your leave has been approved."
            );
        });
    }

    // =========================================================================
    // REJECT
    // =========================================================================
    @Override
    @Transactional
    public void reject(Long leaveRequestId, String reason) {

        LeaveRequest request = getActiveRequest(leaveRequestId);

        if (request.getStatus() != LeaveStatus.PENDING
                && request.getStatus() != LeaveStatus.WAITING_FOR_DOCUMENT) {
            throw new InvalidRequestException("Cannot reject a request with status: " + request.getStatus());
        }

        // Restore balance
        restoreBalance(request);

        request.setStatus(LeaveStatus.REJECTED);
        request.setRejectionReason(reason);
        leaveRequestRepository.save(request);

        // Notify employee
        personalRepository.findById(request.getPersonalId()).ifPresent(emp -> {
            emailService.sendLeaveStatusUpdate(
                    resolveEmail(emp), fullName(emp),
                    request.getLeaveType().getName(),
                    "REJECTED",
                    request.getStartDate().format(FMT),
                    request.getEndDate().format(FMT),
                    reason
            );
        });
    }

    // =========================================================================
    // CANCEL (with reason)
    // =========================================================================
    @Override
    @Transactional
    public void cancel(Long leaveRequestId) {
        throw new InvalidRequestException("Use cancelWithReason() instead");
    }

    @Transactional
    public void cancelWithReason(Long leaveRequestId, String cancelReason) {

        LeaveRequest request = getActiveRequest(leaveRequestId);

        if (request.getStatus() == LeaveStatus.CANCELLED) {
            throw new InvalidRequestException("Request is already cancelled");
        }
        if (request.getStatus() == LeaveStatus.REJECTED) {
            throw new InvalidRequestException("Cannot cancel a rejected request");
        }

        // Only restore balance if it was PENDING (balance was already deducted on apply)
        // If APPROVED, balance was already deducted — restore it
        if (request.getStatus() == LeaveStatus.PENDING
                || request.getStatus() == LeaveStatus.APPROVED) {
            restoreBalance(request);
        }

        request.setStatus(LeaveStatus.CANCELLED);
        request.setCancelReason(cancelReason);
        request.setIsCancelled(true);
        request.setCancelledAt(LocalDateTime.now());
        leaveRequestRepository.save(request);

        // Notify employee + manager
        personalRepository.findById(request.getPersonalId()).ifPresent(emp -> {
            String empEmail = resolveEmail(emp);
            String empName  = fullName(emp);
            String start    = request.getStartDate().format(FMT);
            String end      = request.getEndDate().format(FMT);

            emailService.sendLeaveStatusUpdate(
                    empEmail, empName,
                    request.getLeaveType().getName(),
                    "CANCELLED", start, end, cancelReason);

            resolveManagerEmail(emp).forEach(mgEmail ->
                    emailService.sendLeaveCancelledManager(
                            mgEmail, empName,
                            request.getLeaveType().getName(),
                            start, end, cancelReason));
        });
    }

    // =========================================================================
    // GET MY LEAVES
    // =========================================================================
    @Override
    public PageResponseDTO<LeaveRequestResponseDTO> getMyLeaves(Long personalId, Pageable pageable) {

        PersonalInformation personal = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Page<LeaveRequest> page = leaveRequestRepository
                .findByPersonalIdOrderByCreatedAtDesc(personalId, pageable);

        return buildPage(page, personal);
    }

    // =========================================================================
    // GET ALL (Admin/HR)
    // =========================================================================
    @Override
    public PageResponseDTO<LeaveRequestResponseDTO> getAll(Pageable pageable) {

        Page<LeaveRequest> page = leaveRequestRepository
                .findAllByOrderByCreatedAtDesc(pageable);

        return PageResponseDTO.<LeaveRequestResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(r -> {
                            PersonalInformation p = personalRepository
                                    .findById(r.getPersonalId()).orElse(null);
                            return toResponse(r, p);
                        }).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // =========================================================================
    // REQUEST DOCUMENT
    // =========================================================================
    @Override
    @Transactional
    public void requestDocument(Long leaveRequestId) {
        LeaveRequest request = getActiveRequest(leaveRequestId);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Can only request documents for PENDING leaves");
        }
        request.setStatus(LeaveStatus.WAITING_FOR_DOCUMENT);
        leaveRequestRepository.save(request);
    }

    // ================= DYNAMIC APPROVAL =================
    private boolean isFinalApproval(LeaveRequest req) {

        int requiredLevels = 2; // unchanged

        return req.getApprovalLevel() >= requiredLevels;
    }

    // ================= PARTIAL CANCEL =================
    public void partialCancel(Long leaveId, LocalDate from, LocalDate to) {

        LeaveRequest req = get(leaveId);

        double cancelDays = calculateCancelDays(from, to);

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() //  FIX
        );

        leaveAttendanceService.revertLeaveAttendance(req);
    }

    // ================= REJECT =================
/*
    @Override
    public void reject(Long id, String reason) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.APPROVED) {
            throw new InvalidRequestException("Already approved");
        }

        req.setStatus(LeaveStatus.REJECTED);
        req.setRejectionReason(reason);

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() //  FIX
        );
        leaveRequestRepository.save(req);
    }
*/

    // ================= CANCEL =================
/*    @Override
    public void cancel(Long id) {

        LeaveRequest req = get(id);

        req.setStatus(LeaveStatus.CANCELLED);
        req.setIsCancelled(true);
        req.setCancelledAt(LocalDateTime.now());

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() // FIX
        );

        leaveAttendanceService.revertLeaveAttendance(req);

        leaveRequestRepository.save(req);
    }*/

    // ================= GET =================
/*    @Override
    public PageResponseDTO<LeaveRequestResponseDTO> getMyLeaves(
            Long personalId,
            Pageable pageable) {

        Page<LeaveRequest> page =
                leaveRequestRepository.findByPersonalId(personalId, pageable);

        List<LeaveRequestResponseDTO> content = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponseDTO.<LeaveRequestResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }*/

    // ================= VALIDATION =================

    private void validateOverlap(LeaveRequestDTO dto) {

        boolean exists = leaveRequestRepository
                .existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        dto.getPersonalId(),
                        dto.getEndDate(),
                        dto.getStartDate()
                );

        if (exists) {
            throw new InvalidRequestException("Leave already exists in this date range");
        }
    }

/*
    @Override
    public PageResponseDTO<LeaveRequestResponseDTO> getAll(Pageable pageable) {

        Page<LeaveRequest> page = leaveRequestRepository.findAll(pageable);

        List<LeaveRequestResponseDTO> content = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponseDTO.<LeaveRequestResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
*/

    private void validatePolicyMapping(Long policyId, Long leaveTypeId) {

        mappingRepository
                .findByLeavePolicyIdAndLeaveTypeIdAndIsActiveTrue(policyId, leaveTypeId)
                .orElseThrow(() -> new InvalidRequestException("Leave type not allowed in policy"));
    }

    // ================= COMMON =================

    private LeaveRequest get(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
    }

    private double calculateCancelDays(LocalDate from, LocalDate to) {

        double days = 0;
        LocalDate date = from;

        while (!date.isAfter(to)) {
            days++;
            date = date.plusDays(1);
        }

        return days;
    }

    // 🔥 SAME OLD METHOD (UNCHANGED)
    private Long getPolicyId(Long personalId) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Work profile not found"));

        EmploymentType type = profile.getPersonalInformation().getEmploymentType();

        LeavePolicy policy = policyRepository
                .findByEmploymentTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found"));

        return policy.getId();
    }

    private void checkAndDeductBalance(PersonalInformation personal,
                                       LeaveType leaveType,
                                       LeavePolicy policy,
                                       double totalDays) {
        int year = LocalDate.now().getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByPersonalIdAndLeaveTypeIdAndYear(personal.getId(), leaveType.getId(), year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not found for: " + leaveType.getName()));

        if (balance.getRemainingLeaves() < totalDays) {
            throw new InvalidRequestException(
                    "Insufficient balance. Available: " + balance.getRemainingLeaves()
                            + ", Requested: " + totalDays);
        }

        double before = balance.getRemainingLeaves();
        double after  = before - totalDays;

        balance.setUsedLeaves(balance.getUsedLeaves() + totalDays);
        balance.setRemainingLeaves(after);
        leaveBalanceRepository.save(balance);

        // Log transaction
        transactionService.log(balance, LeaveTransactionType.APPLY,
                totalDays, before, after, null, "Leave applied");
    }

    private void restoreBalance(LeaveRequest request) {
        int year = request.getStartDate().getYear();

        leaveBalanceRepository
                .findByPersonalIdAndLeaveTypeIdAndYear(
                        request.getPersonalId(), request.getLeaveType().getId(), year)
                .ifPresent(balance -> {
                    double before = balance.getRemainingLeaves();
                    double after  = before + request.getTotalDays();

                    balance.setUsedLeaves(Math.max(0, balance.getUsedLeaves() - request.getTotalDays()));
                    balance.setRemainingLeaves(after);
                    leaveBalanceRepository.save(balance);

                    transactionService.log(balance, LeaveTransactionType.CANCEL,
                            request.getTotalDays(), before, after,
                            request.getId(), "Leave cancelled/rejected — balance restored");
                });
    }

    private double calculateWorkingDays(LocalDate start, LocalDate end,
                                        DayType startType, DayType endType) {
        if (end.isBefore(start)) return 0;

        double days = 0;
        LocalDate cur = start;

        while (!cur.isAfter(end)) {
            DayOfWeek dow = cur.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                days++;
            }
            cur = cur.plusDays(1);
        }

        // Adjust half-days
        if (startType != DayType.FULL) days -= 0.5;
        if (endType   != DayType.FULL && !start.equals(end)) days -= 0.5;

        return Math.max(0.5, days);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new InvalidRequestException("End date cannot be before start date");
        }
    }

    private LeaveRequest getActiveRequest(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
    }

    private PageResponseDTO<LeaveRequestResponseDTO> buildPage(
            Page<LeaveRequest> page, PersonalInformation personal) {

        return PageResponseDTO.<LeaveRequestResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(r -> toResponse(r, personal)).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private LeaveRequestResponseDTO toResponse(LeaveRequest r, PersonalInformation p) {
        return LeaveRequestResponseDTO.builder()
                .id(r.getId())
                .personalId(r.getPersonalId())
                .employeeName(p != null ? fullName(p) : "Unknown")
                .employeeCode(resolveCode(p))
                .leaveType(r.getLeaveType() != null ? r.getLeaveType().getName() : "")
                .leaveTypeCode(r.getLeaveType() != null ? r.getLeaveType().getCode() : "")
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .startDayType(r.getStartDayType() != null ? r.getStartDayType().name() : "")
                .endDayType(r.getEndDayType() != null ? r.getEndDayType().name() : "")
                .totalDays(r.getTotalDays())
                .reason(r.getReason())
                .status(r.getStatus() != null ? r.getStatus().name() : "")
                .appliedOn(r.getCreatedAt())
                .approvedAt(r.getApprovedAt() != null ? r.getApprovedAt().toString() : null)
                .rejectionReason(r.getRejectionReason())
                .cancelReason(r.getCancelReason())
                .cancelledAt(r.getCancelledAt() != null ? r.getCancelledAt().toString() : null)
                .build();
    }

    private String fullName(PersonalInformation p) {
        if (p == null) return "Unknown";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? " " + p.getMiddleName() : "";
        return p.getFirstName() + mid + " " + p.getLastName();
    }

    private String resolveCode(PersonalInformation p) {
        if (p == null) return "";
        if (p.getEmployee() != null) return p.getEmployee().getEmployeeCode();
        if (p.getIntern()   != null) return p.getIntern().getInternCode();
        if (p.getTrainee()  != null) return p.getTrainee().getTraineeCode();
        return "";
    }

    private String resolveEmail(PersonalInformation p) {
        if (p != null && p.getContact() != null) {
            return p.getContact().getOfficeEmail() != null
                    ? p.getContact().getOfficeEmail()
                    : p.getContact().getPersonalEmail();
        }
        return "";
    }

    private List<String> resolveManagerEmail(PersonalInformation p) {
        if (p != null
                && p.getWorkProfile() != null
                && p.getWorkProfile().getReportingManager() != null) {

            PersonalInformation mgr = p.getWorkProfile()
                    .getReportingManager()
                    .getPersonalInformation();

            return mgr != null ? List.of(resolveEmail(mgr)) : List.of();
        }
        return List.of();
    }


    // ================= DOCUMENT REQUEST =================
/*    @Override
    public void requestDocument(Long id) {

        LeaveRequest req = get(id);

        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Only pending leave can request document");
        }

        req.setStatus(LeaveStatus.WAITING_FOR_DOCUMENT);

        leaveRequestRepository.save(req);
    }*/
}