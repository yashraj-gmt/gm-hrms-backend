package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
public class BaseUserMapper {

    private BaseUserMapper() {}

    public static void mapCommon(
            BaseUserResponseDTO dto,
            PersonalInformation p
    ) {
        if (p == null) return;

        mapPersonal(dto, p);
        mapWorkProfile(dto, p.getWorkProfile());
        mapContactAndBank(dto, p);
        mapAddress(dto, p);
    }

    // ================= PERSONAL =================

    private static void mapPersonal(BaseUserResponseDTO dto, PersonalInformation p) {

        dto.setPersonalInformationId(p.getId());
        dto.setFirstName(p.getFirstName());
        dto.setMiddleName(p.getMiddleName());
        dto.setLastName(p.getLastName());
        dto.setGender(p.getGender());
        dto.setDateOfBirth(p.getDateOfBirth());
        dto.setMaritalStatus(p.getMaritalStatus());
        dto.setSpouseOrParentName(p.getSpouseOrParentName());
        dto.setActive(p.getActive());
    }

    // ================= WORK PROFILE =================

    private static void mapWorkProfile(BaseUserResponseDTO dto, WorkProfile wp) {

        if (wp == null) return;

        dto.setDepartmentName(
                wp.getDepartment() != null
                        ? wp.getDepartment().getName()
                        : null
        );

        dto.setDesignationName(
                wp.getDesignation() != null
                        ? wp.getDesignation().getName()
                        : null
        );

        dto.setReportingManagerName(
                wp.getReportingManager() != null &&
                        wp.getReportingManager().getPersonalInformation() != null
                        ? wp.getReportingManager().getPersonalInformation().getFirstName()
                        : null
        );

        dto.setStatus(wp.getStatus());
    }

    // ================= CONTACT + BANK =================

    private static void mapContactAndBank(BaseUserResponseDTO dto, PersonalInformation p) {

        dto.setContact(mapContact(p.getContact()));
        dto.setBankDetails(mapBank(p.getBankLegalDetails()));
    }

    // ================= ADDRESS =================

    private static void mapAddress(BaseUserResponseDTO dto, PersonalInformation p) {

        dto.setCurrentAddress(AddressMapper.toResponse(p.getCurrentAddress()));
        dto.setPermanentAddress(AddressMapper.toResponse(p.getPermanentAddress()));
    }

    // ================= REUSABLE =================

    public static ContactResponseDTO mapContact(PersonalInformationContact contact) {

        if (contact == null) return null;

        return ContactResponseDTO.builder()
                .personalPhone(contact.getPersonalPhone())
                .emergencyPhone(contact.getEmergencyPhone())
                .personalEmail(contact.getPersonalEmail())
                .officeEmail(contact.getOfficeEmail())
                .build();
    }

    public static EmployeeBankDetailsResponseDTO mapBank(BankLegalDetails bank) {

        if (bank == null) return null;

        return EmployeeBankDetailsResponseDTO.builder()
                .bankName(bank.getBankName())
                .accountNumber(bank.getAccountNumber())
                .ifscCode(bank.getIfscCode())
                .panNumber(bank.getPanNumber())
                .aadhaarNumber(bank.getAadhaarNumber())
                .uanNumber(bank.getUanNumber())
                .esicNumber(bank.getEsicNumber())
                .pfNumber(bank.getPfNumber())
                .build();
    }
}