package com.gm.hrms.repository;

import com.gm.hrms.entity.InternshipDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternshipDomainRepository
        extends JpaRepository<InternshipDomain, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<InternshipDomain> findByNameIgnoreCase(String name);
}