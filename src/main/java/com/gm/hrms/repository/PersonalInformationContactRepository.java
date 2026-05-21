package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformationContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalInformationContactRepository
        extends JpaRepository<PersonalInformationContact, Long> {

    // ── Used by PersonalInformationServiceImpl (exact match, case-sensitive) ──

    boolean existsByPersonalEmail(String personalEmail);

    boolean existsByOfficeEmail(String officeEmail);

    @Query("SELECT COUNT(c) > 0 FROM PersonalInformationContact c " +
            "WHERE LOWER(c.officeEmail) = LOWER(:email)")
    boolean existsByOfficeEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT COUNT(c) > 0 FROM PersonalInformationContact c " +
            "WHERE LOWER(c.personalEmail) = LOWER(:email)")
    boolean existsByPersonalEmailIgnoreCase(@Param("email") String email);
}