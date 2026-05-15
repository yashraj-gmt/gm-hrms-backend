package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankLegalDetailsResponseDTO {

    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String panNumber;
    private String aadhaarNumber;
    private String uanNumber;
    private String esicNumber;
}