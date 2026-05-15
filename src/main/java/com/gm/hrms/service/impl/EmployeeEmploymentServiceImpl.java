package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeEmploymentRequestDTO;
import com.gm.hrms.dto.response.EmployeeEmploymentResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeEmployment;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.EmployeeEmploymentRepository;
import com.gm.hrms.repository.WorkProfileRepository;
import com.gm.hrms.service.EmployeeEmploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeEmploymentServiceImpl implements EmployeeEmploymentService {

    private final EmployeeEmploymentRepository repository;
    private final WorkProfileRepository wpRepository;

    @Override
    public void saveOrUpdate(Employee employee,
                             EmployeeEmploymentRequestDTO dto) {

        if (dto == null) return;

        boolean isDraft =
                employee.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        EmployeeEmployment employment =
                repository.findByEmployee(employee).orElse(null);

        // ================= MERGE VALIDATION =================

        if (!isDraft) {

            LocalDate joining = dto.getDateOfJoining() != null
                    ? dto.getDateOfJoining()
                    : (employment != null ? employment.getDateOfJoining() : null);

            if (joining == null) {
                throw new InvalidRequestException("Date of joining is required");
            }

            Double ctc = dto.getCtc() != null
                    ? dto.getCtc()
                    : (employment != null ? employment.getCtc() : null);

            if (ctc != null && ctc <= 0) {
                throw new InvalidRequestException("CTC must be greater than 0");
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (employment == null) {
            employment = new EmployeeEmployment();
            employment.setEmployee(employee);
        }

        // ================= PATCH =================

        if (dto.getDateOfJoining() != null) {

            employment.setDateOfJoining(dto.getDateOfJoining());

            Long personalId = employee.getPersonalInformation().getId();

            wpRepository.findByPersonalInformationId(personalId)
                    .ifPresent(wp -> wp.setDateOfJoining(dto.getDateOfJoining()));
        }

        if (dto.getYearOfExperience() != null)
            employment.setYearOfExperience(dto.getYearOfExperience());

        if (dto.getCtc() != null)
            employment.setCtc(dto.getCtc());

        if (dto.getPreviousCompanyNames() != null)
            employment.setPreviousCompanyNames(dto.getPreviousCompanyNames());

        if (dto.getNoticePeriod() != null)
            employment.setNoticePeriod(dto.getNoticePeriod());

        repository.save(employment);

        employee.setEmployment(employment);
    }

    @Override
    public EmployeeEmploymentResponseDTO getByEmployee(Employee employee) {

        EmployeeEmployment employment =
                repository.findByEmployee(employee)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Employment details not found"));

        return mapToResponse(employment);
    }

    private EmployeeEmploymentResponseDTO mapToResponse(EmployeeEmployment employment) {

        return EmployeeEmploymentResponseDTO.builder()
                .dateOfJoining(employment.getDateOfJoining())
                .yearOfExperience(employment.getYearOfExperience())
                .ctc(employment.getCtc())
                .previousCompanyNames(employment.getPreviousCompanyNames())
                .noticePeriod(employment.getNoticePeriod())
                .build();
    }
}