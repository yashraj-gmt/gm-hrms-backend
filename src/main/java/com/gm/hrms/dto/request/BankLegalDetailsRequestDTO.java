package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BankLegalDetailsRequestDTO {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid account number")
    private String accountNumber;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String ifscCode;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number")
    private String panNumber;

    @Pattern(regexp = "^[0-9]{12}$", message = "Invalid Aadhaar number")
    private String aadhaarNumber;

    private String uanNumber;
    private String esicNumber;
    private String pfNumber;
}