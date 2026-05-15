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

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByDocKeyIgnoreCase(String docKey);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByDocKeyIgnoreCaseAndIdNot(String docKey, Long id);

    Optional<DocumentType> findByDocKey(String docKey);

    Page<DocumentType> findAll(Pageable pageable);

    @Query("""
        SELECT d FROM DocumentType d
        WHERE :type MEMBER OF d.applicableTypes
        """)
    Page<DocumentType> findByApplicableType(
            @Param("type") ApplicableType type,
            Pageable pageable
    );

    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(
            ApplicableType applicableType
    );

    long countByActiveTrue();

    long countByActiveFalse();
}