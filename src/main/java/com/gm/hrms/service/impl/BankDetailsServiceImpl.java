package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BankDetailsRequestDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.BankLegalDetailsRepository;
import com.gm.hrms.repository.InternRepository;
import com.gm.hrms.service.BankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class BankDetailsServiceImpl
        implements BankDetailsService {

    private final InternRepository internRepository;
    private final BankLegalDetailsRepository repository;

    @Override
    public void saveOrUpdate(
            PersonalInformation person,
            BankDetailsRequestDTO dto
    ) {

        // ================= GET EXISTING =================

        BankLegalDetails bank =
                repository.findByPersonalInformation(person).orElse(null);

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        // ================= SKIP (NO DATA) =================

        if (dto == null && bank == null) {
            return; // kuch bhi save nahi karna
        }

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft && isBankMandatory(person)) {

            String bankName = dto != null && dto.getBankName() != null
                    ? dto.getBankName()
                    : (bank != null ? bank.getBankName() : null);

            String accountNumber = dto != null && dto.getAccountNumber() != null
                    ? dto.getAccountNumber()
                    : (bank != null ? bank.getAccountNumber() : null);

            String ifsc = dto != null && dto.getIfscCode() != null
                    ? dto.getIfscCode()
                    : (bank != null ? bank.getIfscCode() : null);

            if (isBlank(bankName) ||
                    isBlank(accountNumber) ||
                    isBlank(ifsc)) {

                throw new InvalidRequestException(
                        "Bank details (Bank Name, Account Number, IFSC) are mandatory"
                );
            }
        }

        // ================= CREATE IF NOT EXISTS =================

        if (bank == null) {
            bank = new BankLegalDetails();
            bank.setPersonalInformation(person);
        }

        // ================= PATCH =================

        if (dto != null) {

            if (dto.getBankName() != null)
                bank.setBankName(dto.getBankName());

            if (dto.getAccountNumber() != null)
                bank.setAccountNumber(dto.getAccountNumber());

            if (dto.getIfscCode() != null)
                bank.setIfscCode(dto.getIfscCode());

            if (dto.getPanNumber() != null)
                bank.setPanNumber(dto.getPanNumber());

            if (dto.getAadhaarNumber() != null)
                bank.setAadhaarNumber(dto.getAadhaarNumber());

            if (dto.getUanNumber() != null)
                bank.setUanNumber(dto.getUanNumber());

            if (dto.getEsicNumber() != null)
                bank.setEsicNumber(dto.getEsicNumber());

            if (dto.getPfNumber() != null)
                bank.setPfNumber(dto.getPfNumber());
        }

        repository.save(bank);
        person.setBankLegalDetails(bank);
    }
    private void validateBankIfRequired(
            PersonalInformation person,
            BankDetailsRequestDTO dto,
            BankLegalDetails bank
    ) {

        //  SKIP validation in DRAFT
        if (person.getRecordStatus() == RecordStatus.DRAFT) {
            return;
        }

        var type = person.getEmploymentType();

        boolean isMandatory = false;

        // EMPLOYEE
        if (type == EmploymentType.EMPLOYEE) {
            isMandatory = true;
        }

        // TRAINEE
        if (type == EmploymentType.TRAINEE) {
            isMandatory = true;
        }

        // INTERN (PAID)
        if (type == EmploymentType.INTERN) {

            Intern intern = internRepository
                    .findByPersonalInformation(person)
                    .orElse(null);

            if (intern != null &&
                    intern.getInternshipDetails() != null &&
                    intern.getInternshipDetails().getInternshipType() == InternShipType.PAID) {

                isMandatory = true;
            }
        }

        if (isMandatory) {

            String bankName = dto != null && dto.getBankName() != null
                    ? dto.getBankName()
                    : (bank != null ? bank.getBankName() : null);

            String accountNumber = dto != null && dto.getAccountNumber() != null
                    ? dto.getAccountNumber()
                    : (bank != null ? bank.getAccountNumber() : null);

            String ifsc = dto != null && dto.getIfscCode() != null
                    ? dto.getIfscCode()
                    : (bank != null ? bank.getIfscCode() : null);

            if (isBlank(bankName) ||
                    isBlank(accountNumber) ||
                    isBlank(ifsc)) {

                throw new InvalidRequestException(
                        "Bank details (Bank Name, Account Number, IFSC) are mandatory"
                );
            }
        }
    }

    private boolean isBankMandatory(PersonalInformation person) {

        var type = person.getEmploymentType();

        // EMPLOYEE / TRAINEE → always mandatory
        if (type == EmploymentType.EMPLOYEE || type == EmploymentType.TRAINEE) {
            return true;
        }

        // INTERN → only PAID
        if (type == EmploymentType.INTERN) {

            Intern intern = internRepository
                    .findByPersonalInformation(person)
                    .orElse(null);

            return intern != null &&
                    intern.getInternshipDetails() != null &&
                    intern.getInternshipDetails().getInternshipType() == InternShipType.PAID;
        }

        return false;
    }
}