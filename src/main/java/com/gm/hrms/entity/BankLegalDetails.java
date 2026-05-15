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

    @Column(length = 18, name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(length = 10, name = "pan_number")
    private String panNumber;

    @Column(length = 12, name = "aadhaar_number")
    private String aadhaarNumber;

    @Column(name = "uan_number")
    private String uanNumber;

    @Column(name = "esic_number")
    private String esicNumber;

    @Column(name = "pf_number")
    private String pfNumber;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false, unique = true)
    private PersonalInformation personalInformation;
}