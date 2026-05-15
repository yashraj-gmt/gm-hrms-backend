package com.gm.hrms.repository;

import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternMentorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternMentorRepository
        extends JpaRepository<InternMentorDetails, Long> {

    Optional<InternMentorDetails> findByIntern(Intern intern);
}