package com.gm.hrms.entity;

import com.gm.hrms.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee_employment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "year_of_experience")
    private Integer yearOfExperience;

    private Double ctc;

    @ElementCollection
    @CollectionTable(
            name = "employee_previous_companies",
            joinColumns = @JoinColumn(name = "employee_employment_id")
    )
    private List<String> previousCompanyNames;

    @Column(name = "notice_period")
    private Integer noticePeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
}