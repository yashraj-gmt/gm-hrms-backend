package com.gm.hrms.repository;

import com.gm.hrms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);
    Optional<Department> findByCode(String code);

    boolean existsByName(String name);
    boolean existsByCode(String code);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByCodeAndIdNot(String code, Long id);

    long countByStatus(boolean status);

    @Query(value = """
        SELECT DISTINCT d FROM Department d
        LEFT JOIN d.subDepartments s
        WHERE (
            :search IS NULL OR
            LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(d.code) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
        )
        AND (:status IS NULL OR d.status = :status)
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d) FROM Department d
        LEFT JOIN d.subDepartments s
        WHERE (
            :search IS NULL OR
            LOWER(d.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(d.code) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
        )
        AND (:status IS NULL OR d.status = :status)
        """)
    Page<Department> searchDepartments(
            @Param("search") String  search,
            @Param("status") Boolean status,
            Pageable pageable
    );
}