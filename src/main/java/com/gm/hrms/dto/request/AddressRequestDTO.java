package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressRequestDTO {
    private String addressLine;
    private String city;
    private String district;
    private String landmark;
    private String state;
    private String pinCode;
    private String country;
}