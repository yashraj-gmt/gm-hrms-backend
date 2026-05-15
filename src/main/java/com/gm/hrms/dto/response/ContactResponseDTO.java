package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactResponseDTO {

    private String personalEmail;
    private String officeEmail;
    private String personalPhone;
    private String emergencyPhone;
}
