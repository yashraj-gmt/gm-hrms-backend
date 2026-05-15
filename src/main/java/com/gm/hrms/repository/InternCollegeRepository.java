package com.gm.hrms.repository;

import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternCollegeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternCollegeRepository
        extends JpaRepository<InternCollegeDetails, Long> {

    Optional<InternCollegeDetails> findByIntern(Intern intern);
}