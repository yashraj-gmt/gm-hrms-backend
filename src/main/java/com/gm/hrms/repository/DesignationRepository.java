package com.gm.hrms.repository;

import com.gm.hrms.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    // ── Deleted-aware finders (used by service) ────────────────────────────

    Page<Designation> findByDeletedFalse(Pageable pageable);

    Optional<Designation> findByIdAndDeletedFalse(Long id);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    Optional<Designation> findByNameIgnoreCase(String name);
}