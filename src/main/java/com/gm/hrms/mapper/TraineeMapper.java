package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;

public class TraineeMapper {

    private TraineeMapper() {}

    public static TraineeResponseDTO toResponse(Trainee t) {
        if (t == null) return null;

        PersonalInformation p  = t.getPersonalInformation();
        WorkProfile         wp = (p != null) ? p.getWorkProfile() : null;

        TraineeResponseDTO dto = TraineeResponseDTO.builder()
                .traineeId(t.getId())
                .traineeCode(t.getTraineeCode())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();

        if (p != null) {
            BaseUserMapper.mapCommon(dto, p);
        }

//        if (wp != null) {
//            dto.setBranchName(wp.getBranch() != null ? wp.getBranch().getBranchName() : null);
//            dto.setShiftTiming(wp.getShift() != null ? wp.getShift().getShiftName() : null);
//            dto.setWorkMode(wp.getWorkMode());
//            dto.setWorkingType(wp.getWorkingType());
//        }

        dto.setTrainingDetails(mapTraining(t.getTrainingDetails(), wp));
        dto.setEducationDetails(mapEducation(t.getEducationDetails()));
        dto.setMentorDetails(mapMentor(t.getMentorDetails()));

        return dto;
    }

    private static TraineeTrainingResponseDTO mapTraining(TraineeTrainingDetails td, WorkProfile wp) {
        if (td == null) return null;
        return TraineeTrainingResponseDTO.builder()
                .startDate(td.getStartDate())
                .endDate(td.getEndDate())
                .trainingPeriodMonths(td.getTrainingPeriodMonths())
                .stipend(td.getStipend())
                .branchName(wp != null && wp.getBranch() != null ? wp.getBranch().getBranchName() : null)
                .shiftTiming(wp != null && wp.getShift() != null ? wp.getShift().getShiftName() : null)
                .workMode(wp != null ? wp.getWorkMode() : null)
                .workingType(wp != null ? wp.getWorkingType() : null)
                .build();
    }

    private static TraineeEducationResponseDTO mapEducation(TraineeEducationDetails ed) {
        if (ed == null) return null;
        return TraineeEducationResponseDTO.builder()
                .hscCompletion(ed.getHscCompletion())
                .hscYear(ed.getHscYear())
                .bachelorCompletion(ed.getBachelorCompletion())
                .bachelorYear(ed.getBachelorYear())
                .masterCompletion(ed.getMasterCompletion())
                .masterYear(ed.getMasterYear())
                .degreeName(ed.getDegreeName())
                .degreeResult(ed.getDegreeResult())
                .universityName(ed.getUniversityName())
                .universityAddress(ed.getUniversityAddress())
                .trainingCompletionStatus(ed.getTrainingCompletionStatus())
                .build();
    }

    private static TraineeMentorResponseDTO mapMentor(TraineeMentorDetails md) {
        if (md == null) return null;

        String mentorName = null, mentorDesig = null;
        String supName    = null, supDesig    = null;

        if (md.getMentor() != null) {
            PersonalInformation mp = md.getMentor().getPersonalInformation();
            if (mp != null) mentorName = mp.getFirstName() + " " + mp.getLastName();
            WorkProfile mwp = (mp != null) ? mp.getWorkProfile() : null;
            if (mwp != null && mwp.getDesignation() != null)
                mentorDesig = mwp.getDesignation().getName();
        }

        if (md.getSupervisor() != null) {
            PersonalInformation sp = md.getSupervisor().getPersonalInformation();
            if (sp != null) supName = sp.getFirstName() + " " + sp.getLastName();
            WorkProfile swp = (sp != null) ? sp.getWorkProfile() : null;
            if (swp != null && swp.getDesignation() != null)
                supDesig = swp.getDesignation().getName();
        }

        return TraineeMentorResponseDTO.builder()
                .mentorEmployeeId(md.getMentor() != null ? md.getMentor().getId() : null)
                .mentorName(mentorName)
                .mentorDesignation(mentorDesig)
                .supervisorEmployeeId(md.getSupervisor() != null ? md.getSupervisor().getId() : null)
                .supervisorName(supName)
                .supervisorDesignation(supDesig)
                .build();
    }
}