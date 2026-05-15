package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intern_college_details")
@Getter
@Setter
public class InternCollegeDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    @Column(name = "course_name")
    private String courseName;

    private String semester;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "enrollment_number")
    private String enrollmentNumber;

    @Column(name = "college_name")
    private String collegeName;

    @Column(length = 1000, name = "college_address")
    private String collegeAddress;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "degree_completion_status")
    private String degreeCompletionStatus;

    private Integer year;
}