package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "interns")
@Getter
@Setter
public class Intern extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "intern_code")
    private String internCode;

    @OneToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternCollegeDetails collegeDetails;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternInternshipDetails internshipDetails;

    @OneToOne(mappedBy = "intern", cascade = CascadeType.ALL)
    private InternMentorDetails mentorDetails;
}
