package com.gm.hrms.repository;

import com.gm.hrms.entity.Holiday;
import com.gm.hrms.enums.HolidayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    /**
     * Duplicate check — only against non-deleted records.
     * A soft-deleted name+date combination can be reused.
     */
    boolean existsByHolidayNameAndHolidayDateAndIsDeletedFalse(String holidayName, LocalDate holidayDate);

    List<Holiday> findByHolidayDateBetweenAndIsActiveTrueAndIsOptionalFalse(
            LocalDate from,
            LocalDate to
    );


    boolean existsByHolidayDate(LocalDate holidayDate);

    /**
     * Paginated server-side search — excludes soft-deleted records.
     * Supports optional filters: search (name), type, isActive, isOptional.
     */
    @Query("""
        SELECT h FROM Holiday h
        WHERE h.isDeleted = false
        AND (
            :search IS NULL OR
            LOWER(h.holidayName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
        )
        AND (:type       IS NULL OR h.holidayType = :type)
        AND (:isActive   IS NULL OR h.isActive    = :isActive)
        AND (:isOptional IS NULL OR h.isOptional  = :isOptional)
    """)
    Page<Holiday> search(
            @Param("search")     String      search,
            @Param("type") HolidayType type,
            @Param("isActive")   Boolean     isActive,
            @Param("isOptional") Boolean     isOptional,
            Pageable pageable
    );

    // ── Stats counts (non-deleted only) ──────────────────────────────────────
    long countByIsDeletedFalse();
    long countByIsDeletedFalseAndIsActiveTrue();
    long countByIsDeletedFalseAndIsOptionalTrue();
    long countByIsDeletedFalseAndHolidayDateBetween(LocalDate from, LocalDate to);

    // ── Used by leave-calendar logic — updated to exclude deleted records ────
    List<Holiday> findByHolidayDateBetweenAndIsActiveTrueAndIsOptionalFalseAndIsDeletedFalse(
            LocalDate from, LocalDate to
    );
}