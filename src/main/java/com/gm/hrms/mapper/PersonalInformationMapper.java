package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.entity.PersonalInformationContact;
import com.gm.hrms.entity.PersonalInformation;

public class PersonalInformationMapper {

    private PersonalInformationMapper() {}

    // ================= CREATE =================

    public static PersonalInformation toEntity(PersonalInformationRequestDTO dto) {

        return PersonalInformation.builder()
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .employmentType(dto.getEmploymentType())
                .maritalStatus(dto.getMaritalStatus())
                .spouseOrParentName(dto.getSpouseOrParentName())
                .profileImageUrl(dto.getProfileImageUrl())
                .recordStatus(dto.getStatus())
                .active(true)
                .build();
    }

    // ================= PATCH =================

    public static void updateEntity(PersonalInformation entity,
                                    PersonalInformationRequestDTO dto) {

        if (dto.getFirstName() != null)
            entity.setFirstName(dto.getFirstName());

        if (dto.getMiddleName() != null)
            entity.setMiddleName(dto.getMiddleName());

        if (dto.getLastName() != null)
            entity.setLastName(dto.getLastName());

        if (dto.getGender() != null)
            entity.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null)
            entity.setDateOfBirth(dto.getDateOfBirth());

        if (dto.getEmploymentType() != null)
            entity.setEmploymentType(dto.getEmploymentType());

        if (dto.getMaritalStatus() != null)
            entity.setMaritalStatus(dto.getMaritalStatus());

        if (dto.getSpouseOrParentName() != null)
            entity.setSpouseOrParentName(dto.getSpouseOrParentName());

        if (dto.getProfileImageUrl() != null)
            entity.setProfileImageUrl(dto.getProfileImageUrl());
    }

    // ================= RESPONSE =================

    public static PersonalInformationResponseDTO toResponse(
            PersonalInformation personalInformation) {

        PersonalInformationContact contact = personalInformation.getContact();

        return PersonalInformationResponseDTO.builder()
                .id(personalInformation.getId())
                .firstName(personalInformation.getFirstName())
                .middleName(personalInformation.getMiddleName())
                .lastName(personalInformation.getLastName())
                .gender(personalInformation.getGender())
                .dateOfBirth(personalInformation.getDateOfBirth())
                .employmentType(personalInformation.getEmploymentType())
                .maritalStatus(personalInformation.getMaritalStatus())
                .spouseOrParentName(personalInformation.getSpouseOrParentName())
                .profileImageUrl(personalInformation.getProfileImageUrl())
                .status(personalInformation.getRecordStatus())

                // NULL SAFE CONTACT
                .personalPhone(contact != null ? contact.getPersonalPhone() : null)
                .emergencyPhone(contact != null ? contact.getEmergencyPhone() : null)
                .personalEmail(contact != null ? contact.getPersonalEmail() : null)

                .active(personalInformation.getActive())
                .createdAt(personalInformation.getCreatedAt())
                .updatedAt(personalInformation.getUpdatedAt())
                .build();
    }

    public static void updateContact(PersonalInformationContact contact,
                                     PersonalInformationRequestDTO dto) {

        if (dto.getPersonalPhone() != null)
            contact.setPersonalPhone(dto.getPersonalPhone());

        if (dto.getEmergencyPhone() != null)
            contact.setEmergencyPhone(dto.getEmergencyPhone());

        if (dto.getPersonalEmail() != null)
            contact.setPersonalEmail(dto.getPersonalEmail());

        if (dto.getOfficeEmail() != null)
            contact.setOfficeEmail(dto.getOfficeEmail());
    }
}