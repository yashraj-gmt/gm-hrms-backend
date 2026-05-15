package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.repository.PersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveRequestMapper {

    private final PersonalInformationRepository personalRepository;

    // ================= TO ENTITY =================
    public LeaveRequest toEntity(LeaveRequestDTO dto, LeaveType leaveType) {
        return LeaveRequest.builder()
                .personalId(dto.getPersonalId())
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startDayType(dto.getStartDayType())
                .endDayType(dto.getEndDayType())
                .reason(dto.getReason())
                .build();
    }

    // ================= TO RESPONSE =================
    public LeaveRequestResponseDTO toResponse(LeaveRequest lr) {

        String employeeCode = "";
        String name         = "";
        String designation  = "";
        String department   = "";

        // ── Look up PersonalInformation by stored personalId ──
        PersonalInformation personal = personalRepository
                .findById(lr.getPersonalId())
                .orElse(null);

        if (personal != null) {

            name = getFullName(personal);

            // ── EMPLOYEE ──
            if (personal.getEmployee() != null) {
                employeeCode = personal.getEmployee().getEmployeeCode();

                EmployeeEmployment emp = personal.getEmployee().getEmployment();
                if (emp != null) {
                    designation = emp.getDesignation() != null
                            ? emp.getDesignation().getName() : "";
                    department  = emp.getDepartment()  != null
                            ? emp.getDepartment().getName()  : "";
                }

                // ── INTERN ──
            } else if (personal.getIntern() != null) {
                employeeCode = personal.getIntern().getInternCode();

                // ── TRAINEE ──
            } else if (personal.getTrainee() != null) {
                employeeCode = personal.getTrainee().getTraineeCode();
            }
        }

        return LeaveRequestResponseDTO.builder()
                .id(lr.getId())
                .personalId(lr.getPersonalId())
                .employeeCode(employeeCode)
                .employeeName(name)
                .designation(designation)
                .department(department)
                .leaveType(lr.getLeaveType() != null
                        ? lr.getLeaveType().getName() : "")
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .totalDays(lr.getTotalDays() != null ? lr.getTotalDays() : 0)
                .status(lr.getStatus() != null
                        ? lr.getStatus().name() : null)   // ← enum → String
                .appliedOn(lr.getCreatedAt())              // ← from BaseEntity
                .build();
    }

    // ================= HELPERS =================
    private String getFullName(PersonalInformation p) {
        String middle = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? " " + p.getMiddleName() : "";
        return p.getFirstName() + middle + " " + p.getLastName();
    }
}