package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;

public class EmployeeMapper {

    private EmployeeMapper() {}

    // ================= CREATE =================
    public static Employee toEntity(EmployeeRequestDTO dto, String autoCode) {

        if (dto == null) return null;

        return Employee.builder()
                .employeeCode(autoCode)
                .role(dto.getRole())
                .build();
    }

    // ================= RESPONSE =================
    public static EmployeeResponseDTO toResponse(Employee e) {

        if (e == null) return null;

        PersonalInformation p = e.getPersonalInformation();
        WorkProfile wp = (p != null) ? p.getWorkProfile() : null;

        EmployeeResponseDTO dto = EmployeeResponseDTO.builder()
                .employeeId(e.getId())
                .employeeCode(e.getEmployeeCode())
                .role(e.getRole())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();

        // ================= COMMON =================
        if (p != null) {
            BaseUserMapper.mapCommon(dto, p);
        }

        // ================= WORK PROFILE =================
        if (wp != null) {

            dto.setBranchName(
                    wp.getBranch() != null
                            ? wp.getBranch().getBranchName()
                            : null
            );

            dto.setShiftTiming(
                    wp.getShift() != null
                            ? wp.getShift().getShiftName()
                            : null
            );

            dto.setWorkMode(wp.getWorkMode());
            dto.setWorkingType(wp.getWorkingType());
        }

        // ================= EMPLOYMENT =================
        dto.setEmployment(mapEmployment(e.getEmployment()));

        return dto;
    }

    // ================= EMPLOYMENT =================
    private static EmployeeEmploymentResponseDTO mapEmployment(EmployeeEmployment emp) {

        if (emp == null) return null;

        return EmployeeEmploymentResponseDTO.builder()
                .dateOfJoining(emp.getDateOfJoining())
                .yearOfExperience(emp.getYearOfExperience())
                .ctc(emp.getCtc())
                .noticePeriod(emp.getNoticePeriod())
                .previousCompanyNames(emp.getPreviousCompanyNames())
                .build();
    }
}