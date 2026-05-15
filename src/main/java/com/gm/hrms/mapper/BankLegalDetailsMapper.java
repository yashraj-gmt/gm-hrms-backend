package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.dto.response.BankLegalDetailsResponseDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.PersonalInformation;

public class BankLegalDetailsMapper {

    // Convert Request DTO → Entity
    public static BankLegalDetails toEntity(
            BankLegalDetailsRequestDTO dto,
            PersonalInformation personalInformation) {

        return BankLegalDetails.builder()
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .ifscCode(dto.getIfscCode())
                .panNumber(dto.getPanNumber())
                .aadhaarNumber(dto.getAadhaarNumber())
                .uanNumber(dto.getUanNumber())
                .esicNumber(dto.getEsicNumber())
                .pfNumber(dto.getPfNumber())
                .personalInformation(personalInformation)
                .build();
    }

    // Update Existing Entity
    public static void updateEntity(
            BankLegalDetails entity,
            BankLegalDetailsRequestDTO dto) {

        entity.setBankName(dto.getBankName());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setIfscCode(dto.getIfscCode());
        entity.setPanNumber(dto.getPanNumber());
        entity.setPfNumber(entity.getPfNumber());
        entity.setAadhaarNumber(dto.getAadhaarNumber());
        entity.setUanNumber(dto.getUanNumber());
        entity.setEsicNumber(dto.getEsicNumber());
    }

    // Convert Entity → Response DTO
    public static BankLegalDetailsResponseDTO toResponse(
            BankLegalDetails entity) {

        return BankLegalDetailsResponseDTO.builder()
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .ifscCode(entity.getIfscCode())
                .panNumber(entity.getPanNumber())
                .aadhaarNumber(entity.getAadhaarNumber())
                .uanNumber(entity.getUanNumber())
                .esicNumber(entity.getEsicNumber())
                .build();
    }
}