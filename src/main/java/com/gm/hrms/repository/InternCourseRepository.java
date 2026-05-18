package com.gm.hrms.repository;

import com.gm.hrms.entity.InternCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternCourseRepository extends JpaRepository<InternCourse, Long> {

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByNameIgnoreCaseAndIdNotAndDeletedFalse(String name, Long id);

    Page<InternCourse> findByDeletedFalse(Pageable pageable);

    long countByStatusTrueAndDeletedFalse();

    long countByStatusFalseAndDeletedFalse();
}