package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternMentorRequestDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternMentorDetails;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.InternMentorRepository;
import com.gm.hrms.service.InternMentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternMentorServiceImpl implements InternMentorService {

    private final InternMentorRepository repository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void saveOrUpdate(Intern intern,
                             InternMentorRequestDTO dto) {

        boolean isDraft =
                intern.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        InternMentorDetails mentorDetails =
                repository.findByIntern(intern).orElse(null);

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            Long mentorId = dto != null && dto.getMentorEmployeeId() != null
                    ? dto.getMentorEmployeeId()
                    : (mentorDetails != null && mentorDetails.getMentor() != null
                    ? mentorDetails.getMentor().getId()
                    : null);

            if (mentorId == null) {
                throw new InvalidRequestException("Mentor is required");
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (mentorDetails == null) {
            mentorDetails = new InternMentorDetails();
            mentorDetails.setIntern(intern);
        }

        // ================= PATCH =================

        if (dto != null) {

            if (dto.getMentorEmployeeId() != null) {
                Employee mentor = employeeRepository.findById(dto.getMentorEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
                mentorDetails.setMentor(mentor);
            }

            if (dto.getSupervisorEmployeeId() != null) {
                Employee supervisor = employeeRepository.findById(dto.getSupervisorEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));
                mentorDetails.setSupervisor(supervisor);
            }
        }

        repository.save(mentorDetails);
    }
}