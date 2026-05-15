package com.gm.hrms.repository;

import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternInternshipDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternInternshipRepository
        extends JpaRepository<InternInternshipDetails, Long> {

    Optional<InternInternshipDetails> findByIntern(Intern intern);
}