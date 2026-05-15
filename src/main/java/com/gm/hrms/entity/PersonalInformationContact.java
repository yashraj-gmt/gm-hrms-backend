package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person_contacts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "personalEmail")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInformationContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personalPhone;

    private String emergencyPhone;

    @Column(unique = true)
    private String personalEmail;

    @Column(unique = true)
    private String officeEmail;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;
}