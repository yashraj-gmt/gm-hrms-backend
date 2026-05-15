package com.gm.hrms.entity;

import com.gm.hrms.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "personal_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== BASIC INFO =====

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // EMPLOYEE / INTERN / TRAINEE

    @Column(nullable = false)
    private Boolean active = true;

    // ===== MARITAL =====

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus maritalStatus;

    @Column(nullable = false)
    private String spouseOrParentName;

    // ===== PROFILE =====

    @Column(nullable = false)
    private String profileImageUrl;

    // ===== CONTACT =====

    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private PersonalInformationContact contact;

    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private WorkProfile workProfile;

    // Bank Module
    @OneToOne(mappedBy = "personalInformation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private BankLegalDetails bankLegalDetails;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_address_id")
    private Address currentAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permanent_address_id")
    private Address permanentAddress;

    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus; // DRAFT / SUBMITTED


    @OneToOne(mappedBy = "personalInformation", fetch = FetchType.LAZY)
    private Employee employee;

    @OneToOne(mappedBy = "personalInformation", fetch = FetchType.LAZY)
    private Intern intern;

    @OneToOne(mappedBy = "personalInformation", fetch = FetchType.LAZY)
    private Trainee trainee;

}