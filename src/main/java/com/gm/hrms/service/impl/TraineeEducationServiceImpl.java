package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeEducationRequestDTO;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeEducationDetails;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.TraineeEducationRepository;
import com.gm.hrms.service.TraineeEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeEducationServiceImpl implements TraineeEducationService {

    private final TraineeEducationRepository repository;

    @Override
    public void saveOrUpdate(Trainee trainee,
                             TraineeEducationRequestDTO dto) {

        if (dto == null) return;

        boolean isDraft =
                trainee.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        TraineeEducationDetails education =
                repository.findByTrainee(trainee).orElse(null);

        // ================= MERGE VALIDATION =================

        if (!isDraft) {

            Integer hscYear = dto.getHscYear() != null
                    ? dto.getHscYear()
                    : (education != null ? education.getHscYear() : null);

            Integer bachelorYear = dto.getBachelorYear() != null
                    ? dto.getBachelorYear()
                    : (education != null ? education.getBachelorYear() : null);

            Integer masterYear = dto.getMasterYear() != null
                    ? dto.getMasterYear()
                    : (education != null ? education.getMasterYear() : null);

            // ===== REQUIRED (optional if needed) =====
            // if (hscYear == null) throw ...

            // ===== YEAR VALIDATION =====

            if (hscYear != null && bachelorYear != null &&
                    bachelorYear < hscYear) {

                throw new InvalidRequestException("Bachelor year cannot be before HSC year");
            }

            if (bachelorYear != null && masterYear != null &&
                    masterYear < bachelorYear) {

                throw new InvalidRequestException("Master year cannot be before Bachelor year");
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (education == null) {
            education = new TraineeEducationDetails();
            education.setTrainee(trainee);
        }

        // ================= PATCH =================

        if (dto.getHscCompletion() != null)
            education.setHscCompletion(dto.getHscCompletion());

        if (dto.getHscYear() != null)
            education.setHscYear(dto.getHscYear());

        if (dto.getBachelorCompletion() != null)
            education.setBachelorCompletion(dto.getBachelorCompletion());

        if (dto.getBachelorYear() != null)
            education.setBachelorYear(dto.getBachelorYear());

        if (dto.getMasterCompletion() != null)
            education.setMasterCompletion(dto.getMasterCompletion());

        if (dto.getMasterYear() != null)
            education.setMasterYear(dto.getMasterYear());

        if (dto.getDegreeName() != null)
            education.setDegreeName(dto.getDegreeName());

        if (dto.getDegreeResult() != null)
            education.setDegreeResult(dto.getDegreeResult());

        if (dto.getUniversityName() != null)
            education.setUniversityName(dto.getUniversityName());

        if (dto.getUniversityAddress() != null)
            education.setUniversityAddress(dto.getUniversityAddress());

        if (dto.getTrainingCompletionStatus() != null)
            education.setTrainingCompletionStatus(dto.getTrainingCompletionStatus());

        repository.save(education);
    }
}