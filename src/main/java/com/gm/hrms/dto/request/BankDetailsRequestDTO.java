package com.gm.hrms.dto.request;


import lombok.Data;

@Data
public class BankDetailsRequestDTO {

    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String panNumber;
    private String aadhaarNumber;
    private String uanNumber;
    private String esicNumber;
    private String pfNumber;
}