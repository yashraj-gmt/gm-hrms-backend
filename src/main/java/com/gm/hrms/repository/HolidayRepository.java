package com.gm.hrms.repository;

import com.gm.hrms.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByHolidayNameAndHolidayDate(String holidayName, LocalDate holidayDate);

    boolean existsByHolidayDate(LocalDate holidayDate);

    List<Holiday> findByHolidayDateBetweenAndIsActiveTrueAndIsOptionalFalse(
            LocalDate from,
            LocalDate to
    );
}