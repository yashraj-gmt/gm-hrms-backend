package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternInternshipRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternInternshipDetails;
import com.gm.hrms.entity.InternshipDomain;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.InternInternshipRepository;
import com.gm.hrms.repository.InternshipDomainRepository;
import com.gm.hrms.repository.WorkProfileRepository;
import com.gm.hrms.service.InternshipDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipDetailsServiceImpl implements InternshipDetailsService {

    private final InternInternshipRepository repository;
    private final InternshipDomainRepository domainRepository;
    private final WorkProfileRepository workProfileRepository;

    @Override
    public void saveOrUpdate(Intern intern,
                             InternInternshipRequestDTO dto) {

        boolean isDraft =
                intern.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        InternInternshipDetails details =
                repository.findByIntern(intern).orElse(null);

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            Long domainId = dto != null && dto.getDomainId() != null
                    ? dto.getDomainId()
                    : (details != null && details.getDomain() != null
                    ? details.getDomain().getId()
                    : null);

            var start = dto != null && dto.getStartDate() != null
                    ? dto.getStartDate()
                    : (details != null ? details.getStartDate() : null);

            var end = dto != null && dto.getEndDate() != null
                    ? dto.getEndDate()
                    : (details != null ? details.getEndDate() : null);

            Integer training = dto != null && dto.getTrainingPeriodMonths() != null
                    ? dto.getTrainingPeriodMonths()
                    : (details != null ? details.getTrainingPeriodMonths() : null);

            InternShipType type = dto != null && dto.getInternshipType() != null
                    ? dto.getInternshipType()
                    : (details != null ? details.getInternshipType() : null);

            Double stipend = dto != null && dto.getStipend() != null
                    ? dto.getStipend()
                    : (details != null ? details.getStipend() : null);

            // ===== REQUIRED CHECK =====

            if (domainId == null)
                throw new InvalidRequestException("Domain is required");

            if (start == null)
                throw new InvalidRequestException("Start date is required");

            if (end == null)
                throw new InvalidRequestException("End date is required");

            if (training == null)
                throw new InvalidRequestException("Training period is required");

            if (type == null)
                throw new InvalidRequestException("Internship type is required");

            // ===== DATE VALIDATION =====

            if (end.isBefore(start))
                throw new InvalidRequestException("End date cannot be before start date");

            // ===== STIPEND VALIDATION =====

            if (type == InternShipType.PAID && stipend == null) {
                throw new InvalidRequestException("Stipend is required for paid internship");
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (details == null) {
            details = new InternInternshipDetails();
            details.setIntern(intern);
        }

        // ================= PATCH =================

        if (dto != null) {

            if (dto.getDomainId() != null) {
                InternshipDomain domain = domainRepository.findById(dto.getDomainId())
                        .orElseThrow(() -> new ResourceNotFoundException("Internship domain not found"));
                details.setDomain(domain);
            }

            if (dto.getStartDate() != null) {

                details.setStartDate(dto.getStartDate());

                Long personalId = intern.getPersonalInformation().getId();

                workProfileRepository.findByPersonalInformationId(personalId)
                        .ifPresent(wp -> wp.setDateOfJoining(dto.getStartDate()));
            }

            if (dto.getEndDate() != null)
                details.setEndDate(dto.getEndDate());

            if (dto.getTrainingPeriodMonths() != null)
                details.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());

            if (dto.getStipend() != null)
                details.setStipend(dto.getStipend());

            if (dto.getInternshipType() != null)
                details.setInternshipType(dto.getInternshipType());
        }

        repository.save(details);
    }
}