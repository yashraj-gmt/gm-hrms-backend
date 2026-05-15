package com.gm.hrms.repository;

import com.gm.hrms.entity.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    List<TimesheetEntry> findByProject_IdAndTimesheet_WorkDateBetween(
            Long projectId,
            LocalDate start,
            LocalDate end
    );

    List<TimesheetEntry> findByProject_IdAndTimesheet_WorkDate(
            Long projectId,
            LocalDate date
    );
}