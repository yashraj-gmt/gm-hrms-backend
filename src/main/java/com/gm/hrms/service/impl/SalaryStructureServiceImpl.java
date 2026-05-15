package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.SalaryStructureDetailRequestDTO;
import com.gm.hrms.dto.request.SalaryStructureRequestDTO;
import com.gm.hrms.dto.response.SalaryStructureDetailResponseDTO;
import com.gm.hrms.dto.response.SalaryStructureResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final EmployeeSalaryStructureRepository structureRepo;
    private final PersonalInformationRepository     personalRepo;
    private final PayrollComponentRepository        componentRepo;

    @Override
    public SalaryStructureResponseDTO create(Long personalId, SalaryStructureRequestDTO dto) {

        PersonalInformation personal = personalRepo.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (structureRepo.existsByPersonalInformationIdAndEffectiveFrom(
                personalId, dto.getEffectiveFrom())) {
            throw new DuplicateResourceException(
                    "Salary structure already exists for this employee on date " + dto.getEffectiveFrom()
            );
        }

        // deactivate existing
        structureRepo.findActiveStructure(personalId, dto.getEffectiveFrom())
                .ifPresent(existing -> {
                    existing.setEffectiveTo(dto.getEffectiveFrom().minusDays(1));
                    existing.setIsActive(false);
                    structureRepo.save(existing);
                });

        EmployeeSalaryStructure structure = EmployeeSalaryStructure.builder()
                .personalInformation(personal)
                .monthlyCTC(dto.getMonthlyCTC())
                .effectiveFrom(dto.getEffectiveFrom())
                .isActive(true)
                .build();

        structure.setDetails(buildDetails(dto.getDetails(), structure));

        structureRepo.save(structure);
        return toResponse(structure);
    }

    @Override
    public SalaryStructureResponseDTO update(Long structureId, SalaryStructureRequestDTO dto) {
        EmployeeSalaryStructure structure = structureRepo.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found"));

        structure.setMonthlyCTC(dto.getMonthlyCTC());
        structure.getDetails().clear();
        structure.getDetails().addAll(buildDetails(dto.getDetails(), structure));

        structureRepo.save(structure);
        return toResponse(structure);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryStructureResponseDTO getActive(Long personalId) {
        EmployeeSalaryStructure structure =
                structureRepo.findActiveStructure(personalId, LocalDate.now())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No active salary structure found for this employee"));
        return toResponse(structure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryStructureResponseDTO> getHistory(Long personalId) {
        return structureRepo.findByPersonalInformationIdOrderByEffectiveFromDesc(personalId)
                .stream().map(this::toResponse).toList();
    }

    // ───── Helpers ─────

    private List<EmployeeSalaryStructureDetail> buildDetails(
            List<SalaryStructureDetailRequestDTO> dtos,
            EmployeeSalaryStructure structure) {

        return dtos.stream().map(d -> {
            PayrollComponent component = componentRepo.findById(d.getPayrollComponentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Component not found: " + d.getPayrollComponentId()));
            return EmployeeSalaryStructureDetail.builder()
                    .salaryStructure(structure)
                    .payrollComponent(component)
                    .amount(d.getAmount())
                    .build();
        }).toList();
    }

    private SalaryStructureResponseDTO toResponse(EmployeeSalaryStructure s) {
        String name = s.getPersonalInformation().getFirstName()
                + " " + s.getPersonalInformation().getLastName();

        List<SalaryStructureDetailResponseDTO> details = s.getDetails().stream().map(d ->
                SalaryStructureDetailResponseDTO.builder()
                        .id(d.getId())
                        .payrollComponentId(d.getPayrollComponent().getId())
                        .componentName(d.getPayrollComponent().getName())
                        .componentCode(d.getPayrollComponent().getCode())
                        .componentType(d.getPayrollComponent().getType())
                        .amount(d.getAmount())
                        .build()
        ).toList();

        return SalaryStructureResponseDTO.builder()
                .id(s.getId())
                .personalInformationId(s.getPersonalInformation().getId())
                .employeeName(name)
                .monthlyCTC(s.getMonthlyCTC())
                .effectiveFrom(s.getEffectiveFrom())
                .effectiveTo(s.getEffectiveTo())
                .isActive(s.getIsActive())
                .details(details)
                .build();
    }
}