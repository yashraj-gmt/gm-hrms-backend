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

    // @SQLRestriction auto-applies "deleted = false" to all queries below

    Page<DocumentType> findAll(Pageable pageable);

    /**
     * Filter by a single applicable type.
     * Kept for backward compatibility with the /type/{type} endpoint.
     */
    @Query("""
        SELECT d FROM DocumentType d
        WHERE :type MEMBER OF d.applicableTypes
        """)
    Page<DocumentType> findByApplicableType(
            @Param("type") ApplicableType type,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT d FROM DocumentType d
        WHERE EXISTS (
            SELECT t FROM d.applicableTypes t WHERE t IN :types
        )
        """)
    Page<DocumentType> findByApplicableTypesIn(
            @Param("types") Set<ApplicableType> types,
            Pageable pageable
    );

    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(ApplicableType applicableType);

    // Stats — @SQLRestriction ensures deleted records are excluded automatically
    long countByActiveTrue();
    long countByActiveFalse();
}