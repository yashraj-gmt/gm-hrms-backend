package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeWorkDetailsRequestDTO;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeWorkDetails;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.TraineeWorkRepository;
import com.gm.hrms.repository.WorkProfileRepository;
import com.gm.hrms.service.TraineeWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeWorkServiceImpl implements TraineeWorkService {

    private final TraineeWorkRepository repository;
    private final WorkProfileRepository workProfileRepository;


    @Override
    public void saveOrUpdate(Trainee trainee,
                             TraineeWorkDetailsRequestDTO dto) {

        if (dto == null) return;

        boolean isDraft =
                trainee.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        TraineeWorkDetails work =
                repository.findByTrainee(trainee).orElse(null);

        // ================= MERGE VALIDATION =================

        if (!isDraft) {

            var start = dto.getTrainingStartDate() != null
                    ? dto.getTrainingStartDate()
                    : (work != null ? work.getTrainingStartDate() : null);

            var end = dto.getTrainingEndDate() != null
                    ? dto.getTrainingEndDate()
                    : (work != null ? work.getTrainingEndDate() : null);

            Integer months = dto.getTrainingPeriodMonths() != null
                    ? dto.getTrainingPeriodMonths()
                    : (work != null ? work.getTrainingPeriodMonths() : null);

            // ===== REQUIRED CHECK =====

            if (start == null)
                throw new InvalidRequestException("Training start date is required");

            if (end == null)
                throw new InvalidRequestException("Training end date is required");

            if (months == null)
                throw new InvalidRequestException("Training period is required");

            // ===== DATE VALIDATION =====

            if (end.isBefore(start)) {
                throw new InvalidRequestException("Training end date cannot be before start date");
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (work == null) {
            work = new TraineeWorkDetails();
            work.setTrainee(trainee);
        }

        // ================= PATCH =================

        if (dto.getTrainingPeriodMonths() != null)
            work.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());

        if (dto.getTrainingStartDate() != null) {

            work.setTrainingStartDate(dto.getTrainingStartDate());

            Long personalId = trainee.getPersonalInformation().getId();

            workProfileRepository.findByPersonalInformationId(personalId)
                    .ifPresent(wp -> wp.setDateOfJoining(dto.getTrainingStartDate()));
        }

        if (dto.getTrainingEndDate() != null)
            work.setTrainingEndDate(dto.getTrainingEndDate());

        repository.save(work);
    }
}