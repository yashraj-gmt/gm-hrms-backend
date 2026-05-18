package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDTO {
    private String addressLine;
    private String city;
    private String district;
    private String landmark;
    private String state;
    private String pinCode;
    private String country;
}