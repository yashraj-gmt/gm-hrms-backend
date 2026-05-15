package com.gm.hrms.repository;

import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankLegalDetailsRepository
        extends JpaRepository<BankLegalDetails, Long> {

    Optional<BankLegalDetails> findByPersonalInformation(PersonalInformation personalInformation);

    Optional<BankLegalDetails> findByPersonalInformationId(Long personalInformationId);


}