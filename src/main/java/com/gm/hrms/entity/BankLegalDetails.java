package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_legal_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankLegalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name")
    private String bankName;

    // Account numbers can be up to 18 digits — keep 18
    @Column(length = 18, name = "account_number")
    private String accountNumber;

    // IFSC = 4 letters + 0 + 6 alphanumeric = 11 chars → use 11
    @Column(length = 11, name = "ifsc_code")
    private String ifscCode;

    // PAN = exactly 10 chars (ABCDE1234F) → was 10, but varchar(10) in PG
    // is strict so keep 10; the real issue was ifsc/aadhaar being squeezed
    // into the pan column due to a wrong column mapping. Set length = 10.
    @Column(length = 10, name = "pan_number")
    private String panNumber;

    // Aadhaar = 12 digits → was missing explicit length, defaulted to 255 but
    // the column was created as varchar(10) from a previous migration. Set 12.
    @Column(length = 12, name = "aadhaar_number")
    private String aadhaarNumber;

    // UAN = 12 digits
    @Column(length = 12, name = "uan_number")
    private String uanNumber;

    // ESIC = typically 17 chars
    @Column(length = 20, name = "esic_number")
    private String esicNumber;

    // PF number format: XX/XXX/0000000/000/0000000 → up to 22 chars
    @Column(length = 25, name = "pf_number")
    private String pfNumber;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false, unique = true)
    private PersonalInformation personalInformation;
}