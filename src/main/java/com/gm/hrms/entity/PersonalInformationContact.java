
package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "person_contacts",
        uniqueConstraints = {
                // FIX: use snake_case column names (actual DB column names)
                @UniqueConstraint(name = "uk_person_contacts_personal_email", columnNames = "personal_email"),
                @UniqueConstraint(name = "uk_person_contacts_office_email",   columnNames = "office_email")
        }
)
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

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "office_email")
    private String officeEmail;

    @OneToOne
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;
}