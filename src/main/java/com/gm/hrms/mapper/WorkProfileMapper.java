package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.WorkProfileRequestDTO;
import com.gm.hrms.dto.response.WorkProfileResponseDTO;
import com.gm.hrms.entity.*;

public class WorkProfileMapper {

    private WorkProfileMapper(){}

    // ================= CREATE =================

    public static WorkProfile toEntity(
            WorkProfileRequestDTO dto,
            Department department,
            Designation designation,
            Branch branch,
            Shift shift,
            WorkProfile reportingManager
    ){

        return WorkProfile.builder()
                .department(department)
                .designation(designation)
                .branch(branch)
                .shift(shift)
                .reportingManager(reportingManager)
                .workMode(dto.getWorkMode())
                .workingType(dto.getWorkingType())
                .status(dto.getStatus())
                .build();
    }

    // ================= PATCH UPDATE =================

    public static void patchEntity(
            WorkProfile entity,
            WorkProfileRequestDTO dto,
            Department department,
            Designation designation,
            Branch branch,
            Shift shift,
            WorkProfile reportingManager
    ){

        if(department != null)
            entity.setDepartment(department);

        if(designation != null)
            entity.setDesignation(designation);

        if(branch != null)
            entity.setBranch(branch);

        if(shift != null)
            entity.setShift(shift);

        if(reportingManager != null)
            entity.setReportingManager(reportingManager);

        if(dto.getWorkMode() != null)
            entity.setWorkMode(dto.getWorkMode());

        if(dto.getWorkingType() != null)
            entity.setWorkingType(dto.getWorkingType());

        if(dto.getStatus() != null)
            entity.setStatus(dto.getStatus());
    }

    // ================= RESPONSE =================

    public static WorkProfileResponseDTO toResponse(WorkProfile entity){

        return WorkProfileResponseDTO.builder()
                .id(entity.getId())

                .departmentId(
                        entity.getDepartment() != null
                                ? entity.getDepartment().getId()
                                : null
                )

                .designationId(
                        entity.getDesignation() != null
                                ? entity.getDesignation().getId()
                                : null
                )

                .branchId(
                        entity.getBranch() != null
                                ? entity.getBranch().getId()
                                : null
                )

                .shiftId(
                        entity.getShift() != null
                                ? entity.getShift().getId()
                                : null
                )

                .reportingManagerProfileId(
                        entity.getReportingManager() != null
                                ? entity.getReportingManager().getId()
                                : null
                )

                .workMode(entity.getWorkMode())
                .workingType(entity.getWorkingType())
                .status(entity.getStatus())

                .build();
    }
}