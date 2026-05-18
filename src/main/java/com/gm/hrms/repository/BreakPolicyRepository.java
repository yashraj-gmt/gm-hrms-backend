package com.gm.hrms.repository;

import com.gm.hrms.entity.BreakPolicy;
import com.gm.hrms.enums.BreakCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakPolicyRepository extends JpaRepository<BreakPolicy, Long> {

    boolean existsByBreakNameAndIsDeletedFalse(String breakName);

    @Query("""
        SELECT b FROM BreakPolicy b
        WHERE b.isDeleted = false
        AND (
            :search IS NULL OR
            LOWER(b.breakName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
        )
        AND (:category IS NULL OR b.breakCategory = :category)
        AND (:isPaid   IS NULL OR b.isPaid         = :isPaid)
    """)
    Page<BreakPolicy> search(
            @Param("search")   String        search,
            @Param("category") BreakCategory category,
            @Param("isPaid")   Boolean       isPaid,
            Pageable pageable
    );

    long countByIsDeletedFalse();

    long countByIsDeletedFalseAndIsActiveTrue();

    long countByIsDeletedFalseAndBreakCategory(BreakCategory breakCategory);
}