package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.ShiftAssignmentRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AssignmentStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ShiftMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.ShiftAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private final ShiftAssignmentRepository  assignmentRepository;
    private final ShiftRepository            shiftRepository;
    private final PersonalInformationRepository personalInformationRepository;
    private final UserAuthRepository         userAuthRepository;
    private final EmployeeRepository         employeeRepository;
    private final InternRepository           internRepository;
    private final TraineeRepository          traineeRepository;

    // ── ASSIGN ────────────────────────────────────────────────────────────────
    @Override
    public List<ShiftAssignmentResponseDTO> assign(ShiftAssignmentRequestDTO dto) {

        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        boolean isUpcoming = dto.getEffectiveFrom().isAfter(LocalDate.now());
        AssignmentStatus newStatus = isUpcoming
                ? AssignmentStatus.UPCOMING
                : AssignmentStatus.ACTIVE;
        String actor = currentUsername();

        List<ShiftAssignmentResponseDTO> results = new ArrayList<>();

        for (Long piId : dto.getPersonalInformationIds()) {

            PersonalInformation pi = personalInformationRepository.findById(piId)
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + piId));

            // Deactivate current ACTIVE if assigning immediately
            if (!isUpcoming) {
                // ✅ FIXED: use findFirst to avoid NonUniqueResultException
                assignmentRepository
                        .findFirstByPersonalInformationIdAndStatusAndIsActive(
                                piId, AssignmentStatus.ACTIVE, true)
                        .ifPresent(existing -> {
                            existing.setStatus(AssignmentStatus.PREVIOUS);
                            existing.setEffectiveTo(dto.getEffectiveFrom().minusDays(1));
                            existing.setUpdatedAt(LocalDateTime.now());
                            existing.setUpdatedBy(actor);
                            assignmentRepository.save(existing);
                        });
            }

            // Cancel any pending UPCOMING for this person
            // ✅ FIXED: use findFirst to avoid NonUniqueResultException
            assignmentRepository
                    .findFirstByPersonalInformationIdAndStatusAndIsActive(
                            piId, AssignmentStatus.UPCOMING, true)
                    .ifPresent(old -> {
                        old.setIsActive(false);
                        old.setUpdatedAt(LocalDateTime.now());
                        old.setUpdatedBy(actor);
                        assignmentRepository.save(old);
                    });

            ShiftAssignment assignment = ShiftAssignment.builder()
                    .shift(shift)
                    .personalInformation(pi)
                    .effectiveFrom(dto.getEffectiveFrom())
                    .effectiveTo(dto.getEffectiveTo())
                    .status(newStatus)
                    .note(dto.getNote())
                    .isActive(true)
                    .build();
            assignment.setCreatedAt(LocalDateTime.now());
            assignment.setCreatedBy(actor);

            assignmentRepository.save(assignment);
            results.add(toResponse(assignment));
        }

        return results;
    }

    // ── MY CURRENT SHIFT ──────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CurrentShiftResponseDTO getMyCurrentShift() {
        Long piId = currentPersonalInformationId();

        // Auto-promote UPCOMING → ACTIVE if effectiveFrom <= today
        // ✅ FIXED: use findFirst
        assignmentRepository
                .findFirstByPersonalInformationIdAndStatusAndIsActive(
                        piId, AssignmentStatus.UPCOMING, true)
                .ifPresent(upcoming -> {
                    if (!upcoming.getEffectiveFrom().isAfter(LocalDate.now())) {
                        assignmentRepository
                                .findFirstByPersonalInformationIdAndStatusAndIsActive(
                                        piId, AssignmentStatus.ACTIVE, true)
                                .ifPresent(cur -> {
                                    cur.setStatus(AssignmentStatus.PREVIOUS);
                                    cur.setEffectiveTo(upcoming.getEffectiveFrom().minusDays(1));
                                    assignmentRepository.save(cur);
                                });
                        upcoming.setStatus(AssignmentStatus.ACTIVE);
                        assignmentRepository.save(upcoming);
                    }
                });

        // ✅ FIXED: use findFirst
        ShiftAssignment current = assignmentRepository
                .findFirstByPersonalInformationIdAndStatusAndIsActive(
                        piId, AssignmentStatus.ACTIVE, true)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active shift found for your profile"));

        // ✅ FIXED: use findFirst
        Optional<ShiftAssignment> upcomingOpt = assignmentRepository
                .findFirstByPersonalInformationIdAndStatusAndIsActive(
                        piId, AssignmentStatus.UPCOMING, true);

        UpcomingShiftChangeDTO upcomingChange = upcomingOpt.map(u ->
                UpcomingShiftChangeDTO.builder()
                        .newShiftName(u.getShift().getShiftName())
                        .effectiveDate(u.getEffectiveFrom())
                        .assignedBy(u.getCreatedBy())
                        .build()
        ).orElse(null);

        return CurrentShiftResponseDTO.builder()
                .currentAssignment(toResponse(current))
                .upcomingChange(upcomingChange)
                .build();
    }

    // ── MY SHIFT HISTORY ──────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ShiftAssignmentResponseDTO> getMyShiftHistory(Pageable pageable) {
        Long piId = currentPersonalInformationId();
        Page<ShiftAssignment> page = assignmentRepository
                .findAllByPersonalInformationIdAndIsActiveTrueOrderByEffectiveFromDesc(piId, pageable);
        return buildPage(page.getContent().stream().map(this::toResponse).toList(), page);
    }

    // ── GET ALL (Admin / HR) ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ShiftAssignmentResponseDTO> getAll(Pageable pageable) {
        Page<ShiftAssignment> page = assignmentRepository.findAllActive(pageable);
        return buildPage(page.getContent().stream().map(this::toResponse).toList(), page);
    }

    // ── SEARCH ELIGIBLE PERSONS ───────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<EligiblePersonDTO> searchEligiblePersons(String query) {
        String q = (query == null ? "" : query).toLowerCase().trim();

        return personalInformationRepository.findAll().stream()
                .filter(pi -> Boolean.TRUE.equals(pi.getActive()))
                .filter(pi -> {
                    if (q.isEmpty()) return true;
                    String fullName = (pi.getFirstName() + " " + pi.getLastName()).toLowerCase();
                    String code     = resolveCode(pi).toLowerCase();
                    return fullName.contains(q) || code.contains(q);
                })
                .map(pi -> {
                    // ✅ FIXED: use dedicated query that returns List<String>
                    //    — never throws NonUniqueResultException
                    List<String> shiftNames = assignmentRepository
                            .findActiveShiftNameByPersonalInformationId(pi.getId());
                    String currentShift = shiftNames.isEmpty()
                            ? "Unassigned"
                            : shiftNames.get(0);  // take the most recent

                    // Resolve department / designation from WorkProfile if present
                    String department   = "";
                    String designation  = "";
                    if (pi.getWorkProfile() != null) {
                        department  = pi.getWorkProfile().getDepartment()  != null
                                ? pi.getWorkProfile().getDepartment().getName()  : "";
                        designation = pi.getWorkProfile().getDesignation() != null
                                ? pi.getWorkProfile().getDesignation().getName() : "";
                    }

                    return EligiblePersonDTO.builder()
                            .personalInformationId(pi.getId())
                            .code(resolveCode(pi))
                            .fullName(pi.getFirstName() + " " + pi.getLastName())
                            .department(department)
                            .designation(designation)
                            .currentShift(currentShift)
                            .employmentType(pi.getEmploymentType().name())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private ShiftAssignmentResponseDTO toResponse(ShiftAssignment sa) {
        Shift s               = sa.getShift();
        PersonalInformation pi = sa.getPersonalInformation();
        String timing = (s.getTiming() != null)
                ? s.getTiming().getStartTime() + " – " + s.getTiming().getEndTime()
                : "Day-wise Config";

        return ShiftAssignmentResponseDTO.builder()
                .id(sa.getId())
                .shiftId(s.getId())
                .shiftName(s.getShiftName())
                .shiftType(s.getShiftType().name())
                .shiftTiming(timing)
                .personalInformationId(pi.getId())
                .employeeName(pi.getFirstName() + " " + pi.getLastName())
                .employeeCode(resolveCode(pi))
                .effectiveFrom(sa.getEffectiveFrom())
                .effectiveTo(sa.getEffectiveTo())
                .status(sa.getStatus().name())
                .note(sa.getNote())
                .shiftDetails(ShiftMapper.toResponse(s))
                .build();
    }

    private <T> PageResponseDTO<T> buildPage(List<T> content, Page<?> page) {
        return PageResponseDTO.<T>builder()
                .content(content).page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .first(page.isFirst()).last(page.isLast()).build();
    }

    private Long currentPersonalInformationId() {
        String username = currentUsername();
        UserAuth ua = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + username));
        return ua.getPersonalInformation().getId();
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String resolveCode(PersonalInformation pi) {
        return switch (pi.getEmploymentType()) {
            case EMPLOYEE -> employeeRepository
                    .findByPersonalInformationId(pi.getId())
                    .map(Employee::getEmployeeCode).orElse("N/A");
            case INTERN   -> internRepository
                    .findByPersonalInformationId(pi.getId())
                    .map(Intern::getInternCode).orElse("N/A");
            case TRAINEE  -> traineeRepository
                    .findByPersonalInformationId(pi.getId())
                    .map(Trainee::getTraineeCode).orElse("N/A");
        };
    }
}