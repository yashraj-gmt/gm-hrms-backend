package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.TimesheetMonthlyDTO;
import com.gm.hrms.dto.response.TimesheetReportDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.enums.TimesheetStatus;
import com.gm.hrms.mapper.TimesheetReportMapper;
import com.gm.hrms.repository.TimesheetEntryRepository;
import com.gm.hrms.repository.TimesheetRepository;
import com.gm.hrms.service.TimesheetReportService;
import com.gm.hrms.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetReportServiceImpl implements TimesheetReportService {

    private final TimesheetRepository repo;
    private final TimesheetReportMapper mapper;
    private final TimesheetEntryRepository entryRepository;

    @Override
    public List<TimesheetReportDTO> byDateRange(LocalDate s, LocalDate e) {
        return repo.findByWorkDateBetween(s, e)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> byEmployee(Long id, LocalDate s, LocalDate e) {
        return repo.findByPerson_IdAndWorkDateBetween(id, s, e)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> byProject(
            Long projectId,
            LocalDate start,
            LocalDate end
    ) {

        return entryRepository
                .findByProject_IdAndTimesheet_WorkDateBetween(
                        projectId,
                        start,
                        end
                )
                .stream()
                .map(mapper::toProjectDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> byStatus(
            TimesheetStatus status,
            LocalDate s,
            LocalDate e
    ) {
        return repo.findByStatusAndWorkDateBetween(status, s, e)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> todayAll() {
        return repo.findByWorkDate(LocalDate.now())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> todayByEmployee(Long id) {
        return repo.findByPerson_IdAndWorkDateBetween(
                        id,
                        LocalDate.now(),
                        LocalDate.now()
                )
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<TimesheetReportDTO> todayByProject(Long projectId) {

        return entryRepository
                .findByProject_IdAndTimesheet_WorkDate(
                        projectId,
                        LocalDate.now()
                )
                .stream()
                .map(mapper::toProjectDTO)
                .toList();
    }

    @Override
    public List<TimesheetMonthlyDTO> monthly(int m, int y) {

        return repo.findMonthly(m, y)
                .stream()
                .collect(Collectors.groupingBy(t -> t.getPerson().getId()))
                .values()
                .stream()
                .map(this::buildMonthly)
                .toList();
    }

    @Override
    public TimesheetMonthlyDTO monthlyEmployee(Long empId, int m, int y) {

        List<Timesheet> list = repo.findMonthly(m, y)
                .stream()
                .filter(t -> t.getPerson().getId().equals(empId))
                .toList();

        return buildMonthly(list);
    }

    private TimesheetMonthlyDTO buildMonthly(List<Timesheet> list) {

        if (list.isEmpty()) return null;

        PersonalInformation p = list.getFirst().getPerson();

        int totalMinutes = list.stream()
                .mapToInt(t -> t.getTotalMinutes() == null ? 0 : t.getTotalMinutes())
                .sum();

        return TimesheetMonthlyDTO.builder()
                .personId(p.getId())
                .personName(p.getFirstName() + " " + p.getLastName())
                .totalEntries(list.size())
                .totalTime(TimeUtil.toHHmm(totalMinutes))
                .build();
    }

}