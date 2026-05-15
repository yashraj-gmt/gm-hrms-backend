package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.WorkProfileRequestDTO;
import com.gm.hrms.dto.response.WorkProfileResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.WorkProfileMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.WorkProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkProfileServiceImpl implements WorkProfileService {

    private final WorkProfileRepository repository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final BranchRepository branchRepository;
    private final ShiftRepository shiftRepository;
    private final PersonalInformationRepository personalInformationRepository;

    // =====================================================
    // ================= CREATE =============================
    // =====================================================

    @Override
    public WorkProfileResponseDTO create(Long personalInformationId, WorkProfileRequestDTO dto) {

        PersonalInformation person = personalInformationRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        // ================= REQUIRED VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            if (dto.getDepartmentId() == null) {
                throw new InvalidRequestException("Department is required");
            }

            if (dto.getDesignationId() == null) {
                throw new InvalidRequestException("Designation is required");
            }

            if (dto.getBranchId() == null) {
                throw new InvalidRequestException("Branch is required");
            }

            if (dto.getShiftId() == null) {
                throw new InvalidRequestException("Shift is required");
            }

            if (dto.getWorkMode() == null) {
                throw new InvalidRequestException("Work mode is required");
            }

            if (dto.getWorkingType() == null) {
                throw new InvalidRequestException("Working type is required");
            }

            if (dto.getStatus() == null) {
                throw new InvalidRequestException("Status is required");
            }
        }

        // ================= FETCH REFERENCES =================

        Department dept = null;
        Designation desig = null;
        Branch branch = null;
        Shift shift = null;
        WorkProfile reportingManager = null;

        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        if (dto.getDesignationId() != null) {
            desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
        }

        if (dto.getBranchId() != null) {
            branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        if (dto.getShiftId() != null) {
            shift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        }

        if (dto.getReportingManagerProfileId() != null) {
            reportingManager = repository.findById(dto.getReportingManagerProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));
        }

        // ================= SAVE =================

        WorkProfile entity = WorkProfileMapper.toEntity(
                dto,
                dept,
                desig,
                branch,
                shift,
                reportingManager
        );

        entity.setPersonalInformation(person);

        repository.save(entity);

        return WorkProfileMapper.toResponse(entity);
    }

    // =====================================================
    // ================= UPDATE (PATCH) ====================
    // =====================================================

    @Override
    public WorkProfileResponseDTO update(Long id, WorkProfileRequestDTO dto) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        boolean isDraft = entity.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        // ================= MERGE VALIDATION =================

        if (!isDraft) {

            Long deptId = dto.getDepartmentId() != null
                    ? dto.getDepartmentId()
                    : (entity.getDepartment() != null ? entity.getDepartment().getId() : null);

            Long desigId = dto.getDesignationId() != null
                    ? dto.getDesignationId()
                    : (entity.getDesignation() != null ? entity.getDesignation().getId() : null);

            Long branchId = dto.getBranchId() != null
                    ? dto.getBranchId()
                    : (entity.getBranch() != null ? entity.getBranch().getId() : null);

            Long shiftId = dto.getShiftId() != null
                    ? dto.getShiftId()
                    : (entity.getShift() != null ? entity.getShift().getId() : null);

            var workMode = dto.getWorkMode() != null
                    ? dto.getWorkMode()
                    : entity.getWorkMode();

            var workingType = dto.getWorkingType() != null
                    ? dto.getWorkingType()
                    : entity.getWorkingType();

            var status = dto.getStatus() != null
                    ? dto.getStatus()
                    : entity.getStatus();

            // ===== REQUIRED CHECK =====

            if (deptId == null)
                throw new InvalidRequestException("Department is required");

            if (desigId == null)
                throw new InvalidRequestException("Designation is required");

            if (branchId == null)
                throw new InvalidRequestException("Branch is required");

            if (shiftId == null)
                throw new InvalidRequestException("Shift is required");

            if (workMode == null)
                throw new InvalidRequestException("Work mode is required");

            if (workingType == null)
                throw new InvalidRequestException("Working type is required");

            if (status == null)
                throw new InvalidRequestException("Status is required");
        }

        // ================= FETCH REFERENCES =================

        Department dept = null;
        Designation desig = null;
        Branch branch = null;
        Shift shift = null;
        WorkProfile reportingManager = null;

        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        if (dto.getDesignationId() != null) {
            desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
        }

        if (dto.getBranchId() != null) {
            branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        if (dto.getShiftId() != null) {
            shift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        }

        if (dto.getReportingManagerProfileId() != null) {
            reportingManager = repository.findById(dto.getReportingManagerProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));
        }

        // ================= PATCH =================

        WorkProfileMapper.patchEntity(
                entity,
                dto,
                dept,
                desig,
                branch,
                shift,
                reportingManager
        );

        repository.save(entity);

        return WorkProfileMapper.toResponse(entity);
    }
    // =====================================================
    // ================= GET BY ID =========================
    // =====================================================

    @Override
    public WorkProfileResponseDTO getById(Long id) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        return WorkProfileMapper.toResponse(entity);
    }

    // =====================================================
    // ================= GET ALL ===========================
    // =====================================================

    @Override
    public List<WorkProfileResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(WorkProfileMapper::toResponse)
                .toList();
    }

    // =====================================================
    // ================= DELETE ============================
    // =====================================================

    @Override
    public void delete(Long id) {

        WorkProfile entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkProfile not found"));

        repository.delete(entity);
    }
}