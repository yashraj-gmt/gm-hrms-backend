package com.gm.hrms.repository;

import com.gm.hrms.entity.WorkProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkProfileRepository extends JpaRepository<WorkProfile, Long> {
    Optional<WorkProfile> findByPersonalInformationId(Long personalInformationId);
}