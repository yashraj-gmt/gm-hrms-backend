package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.CorrectionRequestSubmitDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository              attendanceRepository;
    private final AttendanceLogRepository           attendanceLogRepository;
    private final AttendanceBreakLogRepository      breakLogRepository;
    private final AttendanceCalculationRepository   calculationRepository;
    private final AttendanceCorrectionRequestRepository correctionRepository;
    private final PersonalInformationRepository     personalInformationRepository;
    private final WorkProfileRepository             workProfileRepository;
    private final UserAuthRepository                userAuthRepository;
    private final EmployeeRepository                employeeRepository;
    private final InternRepository                  internRepository;
    private final TraineeRepository                 traineeRepository;

    // =========================================================================
    //  EMPLOYEE ACTIONS (JWT-resolved)
    // =========================================================================

    @Override
    public AttendanceResponseDTO checkIn() {
        PersonalInformation person = getCurrentPerson();

        if (attendanceRepository.existsByPersonalInformationIdAndAttendanceDate(
                person.getId(), LocalDate.now())) {
            throw new InvalidRequestException("You have already checked in today.");
        }

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(person.getId()).orElse(null);

        Attendance attendance = Attendance.builder()
                .personalInformation(person)
                .workProfile(profile)
                .attendanceDate(LocalDate.now())
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.PRESENT)
                .build();
        attendanceRepository.save(attendance);

        saveLog(person, AttendanceLogType.CHECK_IN);
        return buildResponse(attendance);
    }

    @Override
    public AttendanceResponseDTO checkOut() {
        PersonalInformation person = getCurrentPerson();

        Attendance attendance = getTodayRecord(person.getId());

        if (attendance.getCheckIn() == null) {
            throw new InvalidRequestException("Cannot check out without checking in first.");
        }
        if (attendance.getCheckOut() != null) {
            throw new InvalidRequestException("You have already checked out today.");
        }

        // Auto-end any active break
        closeActiveBreak(attendance.getId());

        attendance.setCheckOut(LocalDateTime.now());
        attendanceRepository.save(attendance);

        saveLog(person, AttendanceLogType.CHECK_OUT);

        AttendanceCalculation calc = calculateAttendance(attendance);
        return buildResponse(attendance);
    }

    @Override
    public AttendanceResponseDTO breakStart() {
        PersonalInformation person = getCurrentPerson();
        Attendance attendance = getTodayRecord(person.getId());

        if (attendance.getCheckIn() == null) {
            throw new InvalidRequestException("Check in first before starting a break.");
        }
        if (attendance.getCheckOut() != null) {
            throw new InvalidRequestException("Cannot start a break after checking out.");
        }

        AttendanceBreakLog last = breakLogRepository
                .findTopByAttendanceIdOrderByBreakStartDesc(attendance.getId());
        if (last != null && last.getBreakEnd() == null) {
            throw new InvalidRequestException("A break is already in progress.");
        }

        breakLogRepository.save(AttendanceBreakLog.builder()
                .attendance(attendance)
                .breakStart(LocalDateTime.now())
                .build());

        return buildResponse(attendance);
    }

    @Override
    public AttendanceResponseDTO breakEnd() {
        PersonalInformation person = getCurrentPerson();
        Attendance attendance = getTodayRecord(person.getId());

        if (attendance.getCheckOut() != null) {
            throw new InvalidRequestException("Cannot end a break after checking out.");
        }

        AttendanceBreakLog breakLog = breakLogRepository
                .findTopByAttendanceIdOrderByBreakStartDesc(attendance.getId());

        if (breakLog == null) {
            throw new InvalidRequestException("No break has been started.");
        }
        if (breakLog.getBreakEnd() != null) {
            throw new InvalidRequestException("Break has already ended.");
        }

        LocalDateTime now = LocalDateTime.now();
        breakLog.setBreakEnd(now);
        int minutes = (int) Duration.between(breakLog.getBreakStart(), now).toMinutes();
        breakLog.setDurationMinutes(Math.max(0, minutes));
        breakLogRepository.save(breakLog);

        return buildResponse(attendance);
    }

    // =========================================================================
    //  QUERIES
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDTO getMyTodayAttendance() {
        PersonalInformation person = getCurrentPerson();
        Attendance att = attendanceRepository
                .findByPersonalInformationIdAndAttendanceDate(person.getId(), LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No attendance record found for today."));
        return buildResponse(att);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDTO getTodayAttendance(Long personalInformationId) {
        if (personalInformationId == null) {
            throw new InvalidRequestException("PersonalInformationId is required.");
        }
        Attendance att = attendanceRepository
                .findByPersonalInformationIdAndAttendanceDate(personalInformationId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No attendance record found for today."));
        return buildResponse(att);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AttendanceResponseDTO> getAllAttendance(
            Pageable pageable, LocalDate date, String status, String department) {

        LocalDate targetDate = date != null ? date : LocalDate.now();

        Page<Attendance> page;
        if (status != null && !status.isBlank()) {
            try {
                AttendanceStatus attStatus = AttendanceStatus.valueOf(status.toUpperCase());
                page = attendanceRepository.findByAttendanceDateAndStatusWithProfile(
                        targetDate, attStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid status value: " + status);
            }
        } else {
            page = attendanceRepository.findByAttendanceDateWithProfile(targetDate, pageable);
        }

        List<AttendanceResponseDTO> content = page.getContent()
                .stream().map(this::buildResponse).toList();

        return buildPageResponse(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AttendanceResponseDTO> getMyHistory(
            Pageable pageable, LocalDate from, LocalDate to, String status) {

        PersonalInformation person = getCurrentPerson();
        LocalDate effectiveFrom = from != null ? from : LocalDate.now().minusMonths(3);
        LocalDate effectiveTo   = to   != null ? to   : LocalDate.now();

        if (effectiveFrom.isAfter(effectiveTo)) {
            throw new InvalidRequestException("'from' date must be before 'to' date.");
        }

        Page<Attendance> page;
        if (status != null && !status.isBlank()) {
            try {
                AttendanceStatus attStatus = AttendanceStatus.valueOf(status.toUpperCase());
                page = attendanceRepository
                        .findByPersonalInformationIdAndDateBetweenAndStatus(
                                person.getId(), effectiveFrom, effectiveTo, attStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid status value: " + status);
            }
        } else {
            page = attendanceRepository
                    .findByPersonalInformationIdAndAttendanceDateBetween(
                            person.getId(), effectiveFrom, effectiveTo, pageable);
        }

        List<AttendanceResponseDTO> content = page.getContent()
                .stream().map(this::buildResponse).toList();

        return buildPageResponse(content, page);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getDailySummary(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();

        Map<AttendanceStatus, Long> counts = new EnumMap<>(AttendanceStatus.class);
        attendanceRepository.countByStatusForDate(targetDate).forEach(row -> {
            AttendanceStatus s = (AttendanceStatus) row[0];
            Long c = (Long) row[1];
            counts.put(s, c);
        });

        long late = attendanceRepository.countLateArrivalsForDate(targetDate);

        long total = counts.values().stream().mapToLong(Long::longValue).sum();

        return AttendanceSummaryDTO.builder()
                .date(targetDate)
                .total((int) total)
                .present((int) counts.getOrDefault(AttendanceStatus.PRESENT,  0L).longValue())
                .absent( (int) counts.getOrDefault(AttendanceStatus.ABSENT,   0L).longValue())
                .halfDay((int) counts.getOrDefault(AttendanceStatus.HALF_DAY, 0L).longValue())
                .onLeave((int) counts.getOrDefault(AttendanceStatus.LEAVE, 0L).longValue())
                .lateArrivals((int) late)
                .build();
    }

    // =========================================================================
    //  ADMIN DIRECT CORRECTION
    // =========================================================================

    @Override
    public AttendanceResponseDTO correctAttendance(AttendanceCorrectionRequestDTO dto) {
        if (dto == null || dto.getAttendanceId() == null) {
            throw new InvalidRequestException("AttendanceId is required.");
        }

        Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found."));

        if (dto.getCheckIn() != null && dto.getCheckOut() != null
                && dto.getCheckOut().isBefore(dto.getCheckIn())) {
            throw new InvalidRequestException("Check-out time cannot be before check-in time.");
        }

        if (dto.getCheckIn()  != null) attendance.setCheckIn(dto.getCheckIn());
        if (dto.getCheckOut() != null) attendance.setCheckOut(dto.getCheckOut());

        attendanceRepository.save(attendance);

        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            calculateAttendance(attendance);
        }

        return buildResponse(attendance);
    }

    // =========================================================================
    //  CORRECTION REQUEST WORKFLOW
    // =========================================================================

    @Override
    public AttendanceCorrectionResponseDTO submitCorrectionRequest(CorrectionRequestSubmitDTO dto) {
        PersonalInformation person = getCurrentPerson();

        if (dto.getAttendanceId() == null) {
            throw new InvalidRequestException("AttendanceId is required.");
        }

        Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found."));

        if (!attendance.getPersonalInformation().getId().equals(person.getId())) {
            throw new InvalidRequestException("You can only request corrections for your own attendance.");
        }

        if (dto.getRequestedCheckIn() != null && dto.getRequestedCheckOut() != null
                && dto.getRequestedCheckOut().isBefore(dto.getRequestedCheckIn())) {
            throw new InvalidRequestException("Requested check-out cannot be before check-in.");
        }

        // Block duplicate PENDING request for same attendance
        if (correctionRepository.existsByAttendanceIdAndStatusIn(
                dto.getAttendanceId(),
                List.of(CorrectionStatus.PENDING, CorrectionStatus.NO_RESPONSE))) {
            throw new InvalidRequestException(
                    "A pending correction request already exists for this attendance record.");
        }

        AttendanceCorrectionRequest req = AttendanceCorrectionRequest.builder()
                .attendance(attendance)
                .personalInformation(person)
                .originalCheckIn(attendance.getCheckIn())
                .originalCheckOut(attendance.getCheckOut())
                .requestedCheckIn(dto.getRequestedCheckIn())
                .requestedCheckOut(dto.getRequestedCheckOut())
                .reason(dto.getReason())
                .status(CorrectionStatus.PENDING)
                .build();

        correctionRepository.save(req);
        return toCorrectionResponse(req);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AttendanceCorrectionResponseDTO> getCorrectionRequests(
            Pageable pageable, String status) {

        Page<AttendanceCorrectionRequest> page;

        if (status != null && !status.isBlank()) {
            try {
                CorrectionStatus cs = CorrectionStatus.valueOf(status.toUpperCase());
                page = correctionRepository.findByStatusOrderByCreatedAtDesc(cs, pageable);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid status value: " + status);
            }
        } else {
            page = correctionRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<AttendanceCorrectionResponseDTO> content = page.getContent()
                .stream().map(this::toCorrectionResponse).toList();

        return buildPageResponse(content, page);
    }

    @Override
    public AttendanceCorrectionResponseDTO approveCorrectionRequest(Long id) {
        AttendanceCorrectionRequest req = findCorrectionRequest(id);
        assertActionable(req);

        Attendance attendance = req.getAttendance();

        if (req.getRequestedCheckIn()  != null) attendance.setCheckIn(req.getRequestedCheckIn());
        if (req.getRequestedCheckOut() != null) attendance.setCheckOut(req.getRequestedCheckOut());
        attendanceRepository.save(attendance);

        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            calculateAttendance(attendance);
        }

        req.setStatus(CorrectionStatus.APPROVED);
        req.setReviewedBy(currentUsername());
        req.setReviewedAt(LocalDateTime.now());
        correctionRepository.save(req);

        return toCorrectionResponse(req);
    }

    @Override
    public AttendanceCorrectionResponseDTO rejectCorrectionRequest(Long id, String remarks) {
        AttendanceCorrectionRequest req = findCorrectionRequest(id);
        assertActionable(req);

        req.setStatus(CorrectionStatus.REJECTED);
        req.setRemarks(remarks);
        req.setReviewedBy(currentUsername());
        req.setReviewedAt(LocalDateTime.now());
        correctionRepository.save(req);

        return toCorrectionResponse(req);
    }

    // =========================================================================
    //  PRIVATE HELPERS
    // =========================================================================

    /** Resolve current authenticated user's PersonalInformation via JWT. */
    private PersonalInformation getCurrentPerson() {
        String username = currentUsername();
        UserAuth ua = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + username));
        if (ua.getPersonalInformation() == null) {
            throw new ResourceNotFoundException(
                    "No personal profile linked to user: " + username);
        }
        return ua.getPersonalInformation();
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Attendance getTodayRecord(Long personId) {
        return attendanceRepository
                .findByPersonalInformationIdAndAttendanceDate(personId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You have not checked in today."));
    }

    private void closeActiveBreak(Long attendanceId) {
        AttendanceBreakLog active = breakLogRepository
                .findTopByAttendanceIdOrderByBreakStartDesc(attendanceId);
        if (active != null && active.getBreakEnd() == null) {
            LocalDateTime now = LocalDateTime.now();
            active.setBreakEnd(now);
            int mins = (int) Duration.between(active.getBreakStart(), now).toMinutes();
            active.setDurationMinutes(Math.max(0, mins));
            breakLogRepository.save(active);
        }
    }

    private void saveLog(PersonalInformation person, AttendanceLogType type) {
        attendanceLogRepository.save(AttendanceLog.builder()
                .personalInformation(person)
                .logTime(LocalDateTime.now())
                .logType(type)
                .deviceType("WEB")
                .build());
    }

    /** Build a full AttendanceResponseDTO from an Attendance entity. */
    private AttendanceResponseDTO buildResponse(Attendance attendance) {
        List<AttendanceBreakLog> breaks =
                breakLogRepository.findByAttendanceId(attendance.getId());
        AttendanceCalculation calc = calculationRepository
                .findByAttendanceId(attendance.getId()).orElse(null);

        PersonalInformation pi  = attendance.getPersonalInformation();
        WorkProfile         wp  = attendance.getWorkProfile();

        String name        = pi != null ? pi.getFirstName() + " " + pi.getLastName() : "";
        String code        = pi != null ? resolveCode(pi) : "";
        String department  = (wp != null && wp.getDepartment()  != null) ? wp.getDepartment().getName()  : "";
        String designation = (wp != null && wp.getDesignation() != null) ? wp.getDesignation().getName() : "";
        String shiftName   = (wp != null && wp.getShift()       != null) ? wp.getShift().getShiftName()  : "";

        boolean isOnBreak = breaks.stream().anyMatch(b -> b.getBreakEnd() == null);

        List<AttendanceResponseDTO.BreakLogDTO> breakDTOs = breaks.stream()
                .map(b -> AttendanceResponseDTO.BreakLogDTO.builder()
                        .id(b.getId())
                        .breakStart(b.getBreakStart())
                        .breakEnd(b.getBreakEnd())
                        .durationMinutes(b.getDurationMinutes())
                        .build())
                .toList();

        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .personalInformationId(pi != null ? pi.getId() : null)
                .employeeCode(code)
                .employeeName(name)
                .designation(designation)
                .department(department)
                .shift(shiftName)
                .attendanceDate(attendance.getAttendanceDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .workMinutes(calc != null && calc.getWorkMinutes()     != null ? calc.getWorkMinutes()     : 0)
                .breakMinutes(calc != null && calc.getBreakMinutes()   != null ? calc.getBreakMinutes()   : 0)
                .lateMinutes(calc != null && calc.getLateMinutes()     != null ? calc.getLateMinutes()     : 0)
                .overtimeMinutes(calc != null && calc.getOvertimeMinutes() != null ? calc.getOvertimeMinutes() : 0)
                .status(attendance.getStatus())
                .isCheckedIn(attendance.getCheckIn()  != null)
                .isOnBreak(isOnBreak)
                .isCheckedOut(attendance.getCheckOut() != null)
                .breakLogs(breakDTOs)
                .build();
    }

    private String resolveCode(PersonalInformation pi) {
        if (pi == null) return "N/A";
        return switch (pi.getEmploymentType()) {
            case EMPLOYEE -> employeeRepository.findByPersonalInformationId(pi.getId())
                    .map(Employee::getEmployeeCode).orElse("N/A");
            case INTERN   -> internRepository.findByPersonalInformationId(pi.getId())
                    .map(Intern::getInternCode).orElse("N/A");
            case TRAINEE  -> traineeRepository.findByPersonalInformationId(pi.getId())
                    .map(Trainee::getTraineeCode).orElse("N/A");
        };
    }

    private AttendanceCalculation calculateAttendance(Attendance attendance) {
        long totalMinutes = Duration.between(
                attendance.getCheckIn(), attendance.getCheckOut()).toMinutes();

        int breakMinutes = breakLogRepository.findByAttendanceId(attendance.getId())
                .stream().mapToInt(b -> b.getDurationMinutes() != null ? b.getDurationMinutes() : 0).sum();

        int workMinutes     = (int) totalMinutes - breakMinutes;
        int lateMinutes     = 0;
        int overtimeMinutes = 0;
        AttendanceStatus status = AttendanceStatus.PRESENT;

        WorkProfile profile = attendance.getWorkProfile();
        if (profile != null && profile.getShift() != null) {
            Shift shift = profile.getShift();

            LocalTime shiftStart = resolveShiftStartTime(shift, attendance.getAttendanceDate());
            if (shiftStart != null) {
                int grace = shift.getGraceMinutes() != null ? shift.getGraceMinutes() : 0;
                LocalDateTime allowed = attendance.getAttendanceDate()
                        .atTime(shiftStart).plusMinutes(grace);
                if (attendance.getCheckIn().isAfter(allowed)) {
                    lateMinutes = (int) Duration.between(allowed, attendance.getCheckIn()).toMinutes();
                }
            }

            if (shift.getMinimumWorkHours() != null) {
                int requiredMinutes = shift.getMinimumWorkHours() * 60;

                if (workMinutes > requiredMinutes) {
                    overtimeMinutes = workMinutes - requiredMinutes;
                }

                int monthlyLate = calculationRepository.countMonthlyLate(
                        attendance.getPersonalInformation().getId(),
                        attendance.getAttendanceDate().getMonthValue(),
                        attendance.getAttendanceDate().getYear());
                if (lateMinutes > 0) monthlyLate++;

                if ((monthlyLate >= 3 && workMinutes < requiredMinutes)
                        || workMinutes < requiredMinutes / 2) {
                    status = AttendanceStatus.HALF_DAY;
                }
            }
        }

        attendance.setStatus(status);
        attendanceRepository.save(attendance);

        // Upsert calculation
        AttendanceCalculation calc = calculationRepository
                .findByAttendanceId(attendance.getId())
                .orElse(AttendanceCalculation.builder().attendance(attendance).build());

        calc.setWorkMinutes(workMinutes);
        calc.setBreakMinutes(breakMinutes);
        calc.setLateMinutes(lateMinutes);
        calc.setOvertimeMinutes(overtimeMinutes);

        return calculationRepository.save(calc);
    }

    private LocalTime resolveShiftStartTime(Shift shift, LocalDate date) {
        if (shift.getShiftType() == ShiftType.NORMAL) {
            return shift.getTiming() != null ? shift.getTiming().getStartTime() : null;
        }
        DayOfWeek today = date.getDayOfWeek();
        return shift.getDayConfigs().stream()
                .filter(d -> d.getDayOfWeek() == today)
                .findFirst()
                .map(ShiftDayConfig::getStartTime)
                .orElse(null);
    }

    private AttendanceCorrectionRequest findCorrectionRequest(Long id) {
        return correctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Correction request not found: " + id));
    }

    private void assertActionable(AttendanceCorrectionRequest req) {
        if (req.getStatus() == CorrectionStatus.APPROVED
                || req.getStatus() == CorrectionStatus.REJECTED) {
            throw new InvalidRequestException(
                    "This request has already been " + req.getStatus().name().toLowerCase() + ".");
        }
    }

    private AttendanceCorrectionResponseDTO toCorrectionResponse(AttendanceCorrectionRequest req) {
        PersonalInformation pi = req.getPersonalInformation();
        WorkProfile wp = pi != null ? pi.getWorkProfile() : null;

        return AttendanceCorrectionResponseDTO.builder()
                .id(req.getId())
                .attendanceDate(req.getAttendance() != null
                        ? req.getAttendance().getAttendanceDate() : null)
                .employeeName(pi != null ? pi.getFirstName() + " " + pi.getLastName() : "")
                .employeeCode(pi != null ? resolveCode(pi) : "")
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : "")
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : "")
                .originalCheckIn(req.getOriginalCheckIn())
                .originalCheckOut(req.getOriginalCheckOut())
                .requestedCheckIn(req.getRequestedCheckIn())
                .requestedCheckOut(req.getRequestedCheckOut())
                .reason(req.getReason())
                .status(req.getStatus().name())
                .remarks(req.getRemarks())
                .reviewedBy(req.getReviewedBy())
                .reviewedAt(req.getReviewedAt())
                .createdAt(req.getCreatedAt())
                .build();
    }

    private <T> PageResponseDTO<T> buildPageResponse(List<T> content, Page<?> page) {
        return PageResponseDTO.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}