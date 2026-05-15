package com.gm.hrms.repository;

import com.gm.hrms.entity.InternCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternCourseRepository extends JpaRepository<InternCourse, Long> {


    // Used on CREATE — checks entire table
    boolean existsByNameIgnoreCase(String name);

    // Used on UPDATE — excludes the record being edited to avoid self-conflict
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // ── List queries ──────────────────────────────────────────────────────────

    // FIX: was findByStatusTrue() — now returns ALL (active + inactive)
    // so soft-deleted records remain visible in the listing with Inactive badge
    Page<InternCourse> findAll(Pageable pageable);

    // ── Stats counts ──────────────────────────────────────────────────────────
    long countByStatusTrue();
    long countByStatusFalse();
}