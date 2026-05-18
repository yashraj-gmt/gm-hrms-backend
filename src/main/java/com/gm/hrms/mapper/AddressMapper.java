package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.entity.Address;

public class AddressMapper {

    private AddressMapper() {}

    public static Address toEntity(AddressRequestDTO dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setAddressLine(dto.getAddressLine()); // ← was missing — root cause
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setLandmark(dto.getLandmark());
        address.setState(dto.getState());
        address.setPinCode(dto.getPinCode());
        address.setCountry(dto.getCountry());
        return address;
    }

    public static void patchEntity(Address address, AddressRequestDTO dto) {
        if (address == null || dto == null) return;
        if (dto.getAddressLine() != null) address.setAddressLine(dto.getAddressLine()); // ← was missing
        if (dto.getCity()        != null) address.setCity(dto.getCity());
        if (dto.getDistrict()    != null) address.setDistrict(dto.getDistrict());
        if (dto.getLandmark()    != null) address.setLandmark(dto.getLandmark());
        if (dto.getState()       != null) address.setState(dto.getState());
        if (dto.getPinCode()     != null) address.setPinCode(dto.getPinCode());
        if (dto.getCountry()     != null) address.setCountry(dto.getCountry());
    }

    public static AddressResponseDTO toResponse(Address address) {
        if (address == null) return null;
        return AddressResponseDTO.builder()
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .district(address.getDistrict())
                .landmark(address.getLandmark())
                .state(address.getState())
                .pinCode(address.getPinCode())
                .country(address.getCountry())
                .build();
    }
}