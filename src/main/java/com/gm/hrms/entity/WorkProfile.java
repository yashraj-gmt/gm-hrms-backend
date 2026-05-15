package com.gm.hrms.entity;

import com.gm.hrms.enums.Status;
import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "work_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ROOT PERSON
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false, unique = true)
    private PersonalInformation personalInformation;

    // ORGANIZATION STRUCTURE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private LocalDate dateOfJoining;


    // REPORTING MANAGER (WORK PROFILE SELF RELATION)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_profile_id")
    private WorkProfile reportingManager;

    // WORK SETTINGS

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    private WorkingType workingType;

    @Enumerated(EnumType.STRING)
    private Status status;
}