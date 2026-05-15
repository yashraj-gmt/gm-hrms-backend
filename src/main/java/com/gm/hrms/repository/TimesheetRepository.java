package com.gm.hrms.repository;

import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.enums.TimesheetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

    Optional<Timesheet> findByPerson_IdAndWorkDate(
            Long personId,
            LocalDate workDate
    );

    List<Timesheet> findByWorkDateBetween(
            LocalDate start,
            LocalDate end
    );

    List<Timesheet> findByPerson_IdAndWorkDateBetween(
            Long personId,
            LocalDate start,
            LocalDate end
    );

    List<Timesheet> findByWorkDate(
            LocalDate date
    );

    List<Timesheet> findByStatusAndWorkDateBetween(
            TimesheetStatus status,
            LocalDate start,
            LocalDate end
    );

    @Query("""
            SELECT t FROM Timesheet t
            WHERE MONTH(t.workDate) = :month
            AND YEAR(t.workDate) = :year
            """)
    List<Timesheet> findMonthly(int month, int year);

}