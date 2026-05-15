package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequestDTO {
    private String name;   // maps to firstName + lastName split on first space
    private String phone;  // personalPhone
}