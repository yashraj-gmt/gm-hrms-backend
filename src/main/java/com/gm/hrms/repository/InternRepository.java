package com.gm.hrms.repository;

import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternRepository extends JpaRepository<Intern, Long> {

    boolean existsByInternCode(String internCode);

    boolean existsByInternCodeAndIdNot(String internCode, Long id);

    Optional<Intern> findByInternCode(String internCode);

    boolean existsByPersonalInformationId(Long personalInformationId);

    Optional<Intern> findByPersonalInformation(PersonalInformation personalInformation);

    Optional<Intern> findByPersonalInformationId(Long personalId);

}