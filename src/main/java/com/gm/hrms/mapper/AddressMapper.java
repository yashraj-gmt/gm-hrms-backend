package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.entity.Address;

public class AddressMapper {

    private AddressMapper() {}

    // ================= TO ENTITY =================
    public static Address toEntity(AddressRequestDTO dto) {

        if (dto == null) return null; //  safety

        return Address.builder()
                .addressLine(dto.getAddressLine())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .landmark(dto.getLandmark())
                .state(dto.getState())
                .pinCode(dto.getPinCode())
                .country(dto.getCountry())
                .build();
    }

    // ================= PATCH =================
    public static void patchEntity(Address entity, AddressRequestDTO dto) {

        if (entity == null || dto == null) return; //  safety

        if (dto.getAddressLine() != null)
            entity.setAddressLine(dto.getAddressLine());

        if (dto.getCity() != null)
            entity.setCity(dto.getCity());

        if (dto.getDistrict() != null)
            entity.setDistrict(dto.getDistrict());

        if (dto.getLandmark() != null)
            entity.setLandmark(dto.getLandmark());

        if (dto.getState() != null)
            entity.setState(dto.getState());

        if (dto.getPinCode() != null)
            entity.setPinCode(dto.getPinCode());

        if (dto.getCountry() != null)
            entity.setCountry(dto.getCountry());
    }

    // ================= TO RESPONSE =================
    public static AddressResponseDTO toResponse(Address entity) {

        if (entity == null) return null; //

        return AddressResponseDTO.builder()
                .id(entity.getId())
                .address(entity.getAddressLine())
                .city(entity.getCity())
                .district(entity.getDistrict())
                .landmark(entity.getLandmark())
                .state(entity.getState())
                .pinCode(entity.getPinCode())
                .country(entity.getCountry())
                .build();
    }
}