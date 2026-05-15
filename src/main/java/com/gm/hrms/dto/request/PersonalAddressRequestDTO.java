package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class PersonalAddressRequestDTO {

    private AddressRequestDTO currentAddress;

    private AddressRequestDTO permanentAddress;

    private Boolean sameAsCurrent;
}