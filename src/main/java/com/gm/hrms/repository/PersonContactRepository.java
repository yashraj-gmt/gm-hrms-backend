package com.gm.hrms.repository;


import com.gm.hrms.entity.PersonalInformationContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonContactRepository extends JpaRepository<PersonalInformationContact, Long> {
    boolean existsByPersonalEmail(String personalEmail);
    boolean existsByOfficeEmail(String officeEmail);
}