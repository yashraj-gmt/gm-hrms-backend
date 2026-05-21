package com.gm.hrms.repository;

import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    boolean existsByNameIgnoreCase(String name);
    boolean existsByDocKeyIgnoreCase(String docKey);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByDocKeyIgnoreCaseAndIdNot(String docKey, Long id);

    Optional<DocumentType> findByDocKey(String docKey);

    // @SQLRestriction("deleted = false") auto-applies to ALL queries below

    Page<DocumentType> findAll(Pageable pageable);

    /**
     * FIX: Filter by a single applicable type AND active=true.
     * Previously returned inactive docs and docs belonging to other types
     * because the query lacked the active check.
     *
     * The @SQLRestriction already excludes deleted=true rows, so we only
     * need to add "AND d.active = true" here.
     */
    @Query("""
        SELECT d FROM DocumentType d
        WHERE :type MEMBER OF d.applicableTypes
          AND d.active = true
        """)
    Page<DocumentType> findByApplicableType(
            @Param("type") ApplicableType type,
            Pageable pageable
    );

    /**
     * FIX: Filter by multiple applicable types AND active=true.
     */
    @Query("""
        SELECT DISTINCT d FROM DocumentType d
        WHERE EXISTS (
            SELECT t FROM d.applicableTypes t WHERE t IN :types
        )
        AND d.active = true
        """)
    Page<DocumentType> findByApplicableTypesIn(
            @Param("types") Set<ApplicableType> types,
            Pageable pageable
    );

    /**
     * Used by PersonalDocumentService to get required documents for validation.
     * FIX: Already filters by active=true — this is correct. Keep as-is.
     */
    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(ApplicableType applicableType);

    // Stats — @SQLRestriction ensures deleted records are excluded automatically
    long countByActiveTrue();
    long countByActiveFalse();
}