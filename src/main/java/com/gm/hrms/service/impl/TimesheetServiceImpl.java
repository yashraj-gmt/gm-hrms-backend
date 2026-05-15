package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TimesheetEntryDTO;
import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.TimesheetStatus;
import com.gm.hrms.mapper.TimesheetMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.TimesheetService;
import com.gm.hrms.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final PersonalInformationRepository personRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TimesheetResponseDTO createOrUpdateTimesheet(TimesheetRequestDTO request){

        boolean isDraft = request.getStatus() == TimesheetStatus.DRAFT;

        //  Required validation ONLY for SUBMIT
        if(!isDraft){
            if(request.getPersonId() == null){
                throw new RuntimeException("Person not found");
            }

            if(request.getWorkDate() == null){
                throw new RuntimeException("Work date is required");
            }

            if(request.getEntries() == null || request.getEntries().isEmpty()){
                throw new RuntimeException("At least one entry is required");
            }
        }

        PersonalInformation person =
                personRepository.findById(request.getPersonId())
                        .orElseThrow(() -> new RuntimeException("Person not found"));

        Timesheet timesheet =
                timesheetRepository.findByPerson_IdAndWorkDate(
                        request.getPersonId(),
                        request.getWorkDate()
                ).orElse(
                        Timesheet.builder()
                                .person(person)
                                .workDate(request.getWorkDate())
                                .entries(new ArrayList<>())
                                .status(TimesheetStatus.DRAFT)
                                .build()
                );

        timesheet.getEntries().clear();

        int totalMinutes = 0;

        //  Handle null entries safely for DRAFT
        if(request.getEntries() != null){

            for(TimesheetEntryDTO entryDTO : request.getEntries()){

                //  Required validation ONLY for SUBMIT
                if(!isDraft){
                    if(entryDTO.getProjectId() == null){
                        throw new RuntimeException("Project is required");
                    }

                    if(entryDTO.getWorkedTime() == null || entryDTO.getWorkedTime().isBlank()){
                        throw new RuntimeException("Worked time is required");
                    }
                }

                // ❗ Skip empty entries in DRAFT
                if(isDraft && (entryDTO.getProjectId() == null || entryDTO.getWorkedTime() == null)){
                    continue;
                }

                Project project =
                        projectRepository.findById(entryDTO.getProjectId())
                                .orElseThrow(() -> new RuntimeException("Project not found"));

                int minutes = TimeUtil.toMinutes(entryDTO.getWorkedTime());

                totalMinutes += minutes;

                TimesheetEntry entry =
                        TimesheetEntry.builder()
                                .timesheet(timesheet)
                                .project(project)
                                .workedMinutes(minutes)
                                .taskName(entryDTO.getTaskName())
                                .description(entryDTO.getDescription())
                                .build();

                timesheet.getEntries().add(entry);
            }
        }

        //  Business validation ONLY for SUBMIT
        if(!isDraft && totalMinutes > 480){
            throw new RuntimeException("Daily work hours cannot exceed 8 hours");
        }

        timesheet.setTotalMinutes(totalMinutes);

        //  Set correct status (DRAFT / SUBMITTED)
        timesheet.setStatus(request.getStatus());

        Timesheet saved = timesheetRepository.save(timesheet);

        return TimesheetMapper.toResponse(saved);
    }

    @Override
    public TimesheetResponseDTO submitTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(LocalDateTime.now());

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO approveTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedAt(LocalDateTime.now());

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO rejectTimesheet(Long timesheetId){

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheet.setStatus(TimesheetStatus.REJECTED);

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO getTimesheetById(Long id){

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        return TimesheetMapper.toResponse(timesheet);
    }

    @Override
    public TimesheetResponseDTO getByPersonAndDate(Long personId, String date){

        Timesheet timesheet = timesheetRepository
                .findByPerson_IdAndWorkDate(
                        personId,
                        LocalDate.parse(date)
                )
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        return TimesheetMapper.toResponse(timesheet);
    }

    @Override
    public PageResponseDTO<TimesheetResponseDTO> getAllTimesheets(Pageable pageable){

        Page<Timesheet> page = timesheetRepository.findAll(pageable);

        List<TimesheetResponseDTO> content = page.getContent()
                .stream()
                .map(TimesheetMapper::toResponse)
                .toList();

        return PageResponseDTO.<TimesheetResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
    @Override
    public void deleteTimesheet(Long id){

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        timesheetRepository.delete(timesheet);
    }

}